package com.rubyhuntersky.indexrebellion.projections.drift

import android.os.Bundle
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.PlateAdjustment
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.toStatString
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Vision
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.drift.towers.HoldingTower
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.android.tower.TowerActivity
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.common.toDuality
import com.rubyhuntersky.vx.toPercent
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.clicks.plusClicks
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.towers.DualityTower
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import kotlin.math.absoluteValue

class DriftActivity : TowerActivity<Vision, Nothing>() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val holding = HoldingTower.plusClicks(HoldingSight::instrumentId)

    private val addHolding =
        Standard.CenteredTextButton<Unit>()
            .mapSight { vision: Vision -> ClickSight(vision.toUnit(), "+ Holding") }
            .handleEvents { interaction.sendAction(Action.AddHolding) }

    private val refreshPrices =
        Standard.CenteredTextButton<Unit>()
            .mapSight { vision: Vision -> ClickSight(vision.toUnit(), "+= Prices") }
            .handleEvents { interaction.sendAction(Action.RefreshPrices) }

    private val holdings = holding.replicate()
        .mapSight { vision: Vision ->
            val drift = (vision as? Vision.Viewing)?.drift ?: DEFAULT_DRIFT
            drift.generalHoldings.map {
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
        }
        .handleEvents { interaction.sendAction(Action.ViewHolding(instrumentId = it.value.topic)) }

    private val holdingsAndControl = holdings and (addHolding shl Span.HALF[refreshPrices])

    private val netHidden =
        WrapTextTower()
            .plusClicks { Unit }
            .handleEvents { interaction.sendAction(Action.ShowNet(true)) }

    private val netVisible =
        WrapTextTower()
            .plusClicks { Unit }
            .handleEvents { interaction.sendAction(Action.ShowNet(false)) }

    private val net =
        DualityTower(netHidden, netVisible)
            .mapSight { vision: Vision ->
                val balance = (vision as? Vision.Viewing)?.netValue
                balance.toDuality()
                    .mapYin { WrapTextSight("(NET)", TextStyle.Highlight6, Orbit.Center) }
                    .mapYang { WrapTextSight(it.toDollarStat(), TextStyle.Highlight6, Orbit.Center) }
            }
            .plusVPad(VPad.Individual(Standard.spacing * 3 / 2, Standard.spacing / 2))

    private val planButton = Standard.CenteredTextButton<Unit>()
        .fixSight(ClickSight(Unit, "\u2202 Plan"))
        .mapSight(Vision::toUnit)
        .handleEvents { interaction.sendAction(Action.ViewPlan) }

    private val adjustments = adjust.replicate()
        .neverEvent<Nothing>()
        .mapSight { vision: Vision ->
            val drift = (vision as? Vision.Viewing)?.drift ?: DEFAULT_DRIFT
            drift.plateAdjustments.toList().filter(PlateAdjustment::isOwnedOrPlanned)
        }

    override val activityTower: Tower<Vision, Nothing> = Standard
        .SectionTower(
            "Adjustments" to ((net hpad Standard.spacing) and adjustments and (planButton shl Span.HALF[refreshPrices])),
            "Holdings" to (holdingsAndControl)
        )
        .plusVPad(VPad.Ceiling(Standard.spacing))

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

        private val adjustStart = TitleSubtitleTower.mapSight(::adjustToTitle)
        private val adjustMiddle = WrapTextTower().mapSight(::adjustToHeld)
        private val adjustEnd = WrapTextTower().mapSight(::adjustToAction)
        private val adjust = adjustStart shl Span.HALF[adjustMiddle] shl Span.THIRD[adjustEnd] pad Standard.spacing

        private fun adjustToTitle(adjust: PlateAdjustment): TitleSubtitleSight {
            val title = adjust.plate.groupTag
            val subtitle = adjust.realValue.toDollarStat()
            return TitleSubtitleSight(title, subtitle)
        }

        private fun adjustToAction(adjust: PlateAdjustment): WrapTextSight {
            val text = when {
                adjust.toPlannedValue > 0 -> "↗ $${adjust.toPlannedValue.absoluteValue.toStatString()}"
                adjust.toPlannedValue < 0 -> "↘ $${adjust.toPlannedValue.absoluteValue.toStatString()}"
                else -> "Hold"
            }
            return WrapTextSight(text, TextStyle.Highlight6, Orbit.TailLit)
        }

        private fun adjustToHeld(adjust: PlateAdjustment): WrapTextSight {
            val held = "Held ${adjust.realPortion.toPercent()}"
            val direction = when {
                adjust.realPortion > adjust.plannedPortion -> ">"
                adjust.realPortion < adjust.plannedPortion -> "<"
                else -> "="
            }
            val planned = "${adjust.plannedPortion.toPercent()} Planned"
            return WrapTextSight("$held\n$direction\n$planned", TextStyle.Subtitle2, Orbit.Center)
        }
    }
}
