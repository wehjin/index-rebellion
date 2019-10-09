package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Vision
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.indexrebellion.spirits.genies.FetchStockSamples
import com.rubyhuntersky.indexrebellion.spirits.genies.showtoast.ShowToast
import com.rubyhuntersky.indexrebellion.spirits.genies.writedrift.WriteDrift
import com.rubyhuntersky.indexrebellion.toMacroValue
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.stockcatalog.StockSample
import com.rubyhuntersky.vx.android.logChanges
import java.util.*

class ViewDriftStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision {
        object Idle : Vision()
        object Reading : Vision()
        data class Viewing(val drift: Drift, val netValue: CashAmount?) : Vision()
    }

    sealed class Action {
        object Init : Action()
        data class Load(val drift: Drift) : Action()
        data class ViewHolding(val instrumentId: InstrumentId) : Action()
        data class Ignore(val ignore: Any?) : Action()
        object AddHolding : Action()
        object ViewPlan : Action()
        object RefreshPrices : Action()
        data class ShowNet(val show: Boolean) : Action()
        data class LoadSamples(val result: StockMarket.Result) : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ViewDriftStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(@Suppress("UNUSED_PARAMETER") maybe: Any?): Boolean = false

private const val REFRESH_PRICES = "refresh-prices"

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Init -> {
        val readDrifts = ReadDrifts.toWish<ReadDrifts, Action>(
            "read",
            onResult = Action::Load,
            onError = { error("ReadDrift: $it") }
        )
        Revision(Vision.Reading, readDrifts)
    }
    vision is Vision.Reading && action is Action.Load -> {
        Revision(Vision.Viewing(action.drift, null), Wish.none(REFRESH_PRICES))
    }
    vision is Vision.Viewing && action is Action.Load -> {
        Revision(Vision.Viewing(action.drift, vision.netValue), Wish.none(REFRESH_PRICES))
    }
    vision is Vision.Viewing && action is Action.ShowNet -> {
        Revision(Vision.Viewing(vision.drift, if (action.show) vision.drift.netValue else null))
    }
    vision is Vision.Viewing && action is Action.ViewHolding -> {
        val viewHolding = edge.wish(
            "view-holding",
            interaction = ViewHoldingStory().logChanges(ViewHoldingStory.groupId),
            startAction = ViewHoldingStory.Action.Init(action.instrumentId),
            endVisionToAction = Action::Ignore
        )
        Revision(vision, Wish.none(REFRESH_PRICES), viewHolding)
    }
    vision is Vision.Viewing && action is Action.AddHolding -> {
        val addHolding = edge.wish(
            "add-holding",
            interaction = ChooseHoldingTypeStory(),
            startAction = null,
            endVisionToAction = Action::Ignore
        )
        Revision(vision, Wish.none(REFRESH_PRICES), addHolding)
    }
    vision is Vision.Viewing && action is Action.ViewPlan -> {
        val viewPlan = edge.wish(
            "view-plan",
            interaction = ViewPlanStory(),
            startAction = ViewPlanStory.Action.Start,
            endVisionToAction = Action::Ignore
        )
        Revision(vision, Wish.none(REFRESH_PRICES), viewPlan)
    }
    vision is Vision.Viewing && action is Action.RefreshPrices -> {
        val symbols = vision.drift.market.instrumentIds
            .filter { it.type == InstrumentType.StockExchange }
            .map(InstrumentId::symbol)
        val refreshPrices = FetchStockSamples(symbols)
            .toWish2(
                REFRESH_PRICES,
                onResult = Action::LoadSamples,
                onError = Action::Ignore
            )
        Revision(vision, refreshPrices)
    }
    vision is Vision.Viewing && action is Action.LoadSamples -> {
        when (val result = action.result) {
            is StockMarket.Result.NetworkError -> {
                logError("MARKET NETWORK ERROR $result")
                val reason = result.reason
                val showToast = ShowToast(reason, longDuration = true)
                    .toWish2("toast", onResult = Action::Ignore, onError = Action::Ignore)
                Revision(vision, showToast)
            }
            is StockMarket.Result.ParseError -> {
                logError("MARKET PARSE ERROR $result")
                val reason = result.reason ?: result.text
                val showToast = ShowToast(reason, longDuration = true)
                    .toWish2("toast", onResult = Action::Ignore, onError = Action::Ignore)
                Revision(vision, showToast)
            }
            is StockMarket.Result.Samples -> {
                val drift = vision.drift.replace(result.samples.toInstrumentSamples())
                val writeDrift = WriteDrift(drift).toWish2(
                    "write-drift",
                    onResult = Action::Ignore,
                    onError = Action::Ignore
                )
                Revision(vision, writeDrift)
            }
        }
    }
    action is Action.Ignore -> Revision(vision)
    else -> {
        logError("BAD REVISION: $action, $vision")
        Revision(vision)
    }
}

private fun List<StockSample>.toInstrumentSamples(): List<InstrumentSample> = map {
    InstrumentSample(
        instrumentId = InstrumentId(it.symbol.toUpperCase().trim(), InstrumentType.StockExchange),
        instrumentName = it.issuer,
        sharePrice = CashAmount(it.sharePrice),
        macroPrice = CashAmount(it.toMacroValue()),
        sampleDate = Date()
    )
}

private fun logError(message: String) = System.err.println("${ViewDriftStory.groupId} $message")

