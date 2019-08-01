package com.rubyhuntersky.indexrebellion.projections.drift

import android.os.Bundle
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
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
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.android.tower.TowerActivity
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.toPercent
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.clicks.plusClicks
import com.rubyhuntersky.vx.tower.additions.extend.extendCeiling
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import kotlin.math.absoluteValue
import com.rubyhuntersky.indexrebellion.projections.Standard.ItemAttributeTower as ItemAttribute

class DriftActivity : TowerActivity<Vision, Nothing>() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val holdingTower = HoldingTower
        .plusClicks(HoldingSight::instrumentId)

    private val addHoldingTower = Standard.CenteredTextButton<Unit>()
        .mapSight { sight: PageSight -> ClickSight(sight.toUnit(), "+ Holding") }
        .handleEvents { interaction.sendAction(Action.AddHolding) }

    private val refreshPricesTower = Standard.CenteredTextButton<Unit>()
        .mapSight { sight: PageSight -> ClickSight(sight.toUnit(), "▶ Prices") }
        .handleEvents { interaction.sendAction(Action.RefreshPrices) }

    private val holdingsControlTower = addHoldingTower.shareEnd(Span.HALF, refreshPricesTower)

    private val allHoldingsTower = holdingTower
        .replicate()
        .mapSight { page: PageSight -> page.holdings }
        .handleEvents {
            interaction.sendAction(Action.ViewHolding(instrumentId = it.value.topic))
        }
        .extendFloor(holdingsControlTower)

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
                        value = it.cashValue!!.value,
                        plate = drift.plating.findPlate(it.instrumentId)
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

    override val activityTower: Tower<Vision, Nothing> = Standard
        .SectionTower(
            Pair("Holdings", balanceHoldingsTower),
            Pair("Adjustments", allAdjustmentsTower.extendFloor(planButtonTower))
        )
        .plusVPad(VPad.Ceiling(Standard.spacing))
        .mapSight { vision: Vision -> (vision as? Vision.Viewing)?.drift ?: DEFAULT_DRIFT }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityInteraction()
    }

    private fun startActivityInteraction() {
        val activityInteraction = ActivityInteraction(ViewDriftStory.groupId, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) = vx.setSight(vision)

    companion object {

        private val adjustmentStart =
            ItemAttribute().mapSight { adjust: PlateAdjustment -> adjust.plate.memberTag..adjust.realValue.toDollarStat() }

        private val adjustmentEnd =
            ItemAttribute(Orbit.TailLit).mapSight { adjust: PlateAdjustment -> adjust.toAction()..adjust.toStatus() }

        private val adjustmentTower =
            adjustmentStart shl Span.HALF[adjustmentEnd] pad Standard.spacing

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

    }
}
