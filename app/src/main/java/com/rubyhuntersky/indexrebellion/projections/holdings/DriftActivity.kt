package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.PlateAdjustment
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.toStatString
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.Action
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.Vision
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.holdings.towers.BalanceTower
import com.rubyhuntersky.indexrebellion.projections.holdings.towers.HoldingTower
import com.rubyhuntersky.indexrebellion.toLabel
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.logEvents
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.coop.additions.mapSight
import com.rubyhuntersky.vx.toPercent
import com.rubyhuntersky.vx.tower.additions.augment.extendCeiling
import com.rubyhuntersky.vx.tower.additions.handleEvent
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.towers.click.plusClicks
import kotlin.math.absoluteValue

class DriftActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coopContentView.setInActivity(this@DriftActivity)
        activityInteraction = ActivityInteraction(this, ViewDriftStory.groupId, this::renderVision)
        lifecycle.addObserver(activityInteraction)
    }

    private val multiHoldingTower = HoldingTower
        .plusClicks(HoldingSight::instrumentId)
        .replicate()
        .mapSight { page: PageSight -> page.holdings }
        .logEvents("NoEventPageSightMultiHoldingTower")
        .handleEvent {
            val action = Action.ViewHolding(instrumentId = it.value.context)
            activityInteraction.sendAction(action)
        }

    private val holdingsContentTower = multiHoldingTower
        .extendCeiling(BalanceTower)
        .mapSight { drift: Drift ->
            PageSight(
                balance = "0,00",
                holdings = drift.generalHoldings.map {
                    HoldingSight(
                        instrumentId = it.instrumentId,
                        name = drift.market.findSample(it.instrumentId)!!.instrumentName,
                        custodians = it.custodians.map(Custodian::toString),
                        count = it.size,
                        symbol = it.instrumentId.symbol,
                        value = it.cashValue!!.value
                    )
                }
            )
        }
        .logEvents("HoldingsContentTower")

    private val pageTower = Standard.SectionTower(
        Pair("Holdings", holdingsContentTower),
        Pair("Adjustments", adjustmentsContentTower)
    )

    private val pageCoop: Coop<Vision.Viewing, Nothing> = pageTower.inCoop().mapSight(Vision.Viewing::drift)

    private val coopContentView = CoopContentView(pageCoop)

    private lateinit var activityInteraction: ActivityInteraction<Vision, Action>

    private fun renderVision(vision: Vision) {
        when (vision) {
            is Vision.Viewing -> coopContentView.setSight(vision)
        }
    }

    companion object {

        private val adjustmentTower = Standard.BodyTower().mapSight { adjustment: PlateAdjustment ->
            val name = adjustment.toName()
            val status = adjustment.toStatus()
            val verb = adjustment.toAction()
            listOf(name, status, verb).joinToString(" | ")
        }

        private fun PlateAdjustment.toAction(): String = when {
            valueDelta < 0 -> "Invest $${valueDelta.absoluteValue.toStatString()}"
            valueDelta > 0 -> "Divest $${valueDelta.toStatString()}"
            else -> "Hold"
        }

        private fun PlateAdjustment.toStatus(): String {
            val planned = "Plan ${plannedPortion.toPercent()}"
            val real = "Held ${realPortion.toPercent()}"
            val separator = when {
                realPortion > plannedPortion -> ">"
                realPortion < plannedPortion -> "<"
                else -> "="
            }
            return "$real $separator $planned"
        }

        private fun PlateAdjustment.toName(): String = "${plate.toLabel()} ${realValue.toDollarStat()}"

        private val adjustmentsContentTower =
            adjustmentTower.replicate().neverEvent<Nothing>().mapSight { drift: Drift ->
                drift.plateAdjustments.toList()
            }
    }
}
