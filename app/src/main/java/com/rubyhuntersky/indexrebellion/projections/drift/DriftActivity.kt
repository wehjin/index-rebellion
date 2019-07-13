package com.rubyhuntersky.indexrebellion.projections.drift

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.PlateAdjustment
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.toStatString
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Vision
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.drift.towers.BalanceTower
import com.rubyhuntersky.indexrebellion.projections.drift.towers.HoldingTower
import com.rubyhuntersky.indexrebellion.toLabel
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.coop.additions.mapSight
import com.rubyhuntersky.vx.toPercent
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.augment.extendCeiling
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.clicks.plusClicks
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import kotlin.math.absoluteValue

class DriftActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val holdingTower = HoldingTower
        .plusClicks(HoldingSight::instrumentId)

    private val addHoldingTower = Standard.CenteredTextButton<Unit>()
        .mapSight(PageSight::toAddHoldingClick)
        .handleEvents {
            interaction.sendAction(Action.AddHolding)
        }


    private val allHoldingsTower = holdingTower
        .replicate()
        .mapSight { page: PageSight -> page.holdings }
        .handleEvents {
            interaction.sendAction(Action.ViewHolding(instrumentId = it.value.topic))
        }
        .extendFloor(addHoldingTower)

    private val balanceHoldingsTower = allHoldingsTower
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

    private val planButtonTower = Standard.CenteredTextButton<Unit>()
        .fixSight(ClickSight(Unit, "\u2202 Plan"))
        .mapSight(Drift::toUnit)
        .handleEvents { interaction.sendAction(Action.ViewPlan) }

    private val allAdjustmentsTower = adjustmentTower
        .replicate()
        .neverEvent<Nothing>()
        .mapSight { drift: Drift -> drift.plateAdjustments.toList() }

    private val pageTower = Standard.SectionTower(
        Pair("Holdings", balanceHoldingsTower),
        Pair("Adjustments", allAdjustmentsTower.extendFloor(planButtonTower))
    )

    private val pageCoop: Coop<Vision.Viewing, Nothing> = pageTower.inCoop().mapSight(Vision.Viewing::drift)

    private val coopContentView = CoopContentView(pageCoop)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityInteraction()
        coopContentView.setInActivity(this@DriftActivity)
    }

    private fun startActivityInteraction() {
        val activityInteraction = ActivityInteraction(ViewDriftStory.groupId, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        if (vision is Vision.Viewing) coopContentView.setSight(vision)
    }

    companion object {

        private val adjustmentTower = Standard.BodyTower().mapSight { adjustment: PlateAdjustment ->
            val name = adjustment.toName()
            val status = adjustment.toStatus()
            val verb = adjustment.toAction()
            listOf(name, status, verb).joinToString(" | ")
        }

        private fun PlateAdjustment.toAction(): String = when {
            toPlannedValue > 0 -> "Invest $${toPlannedValue.absoluteValue.toStatString()}"
            toPlannedValue < 0 -> "Divest $${toPlannedValue.absoluteValue.toStatString()}"
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


    }
}
