package com.rubyhuntersky.indexrebellion.interactions.main

import android.util.Log
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.report.Correction
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.data.report.RebellionReport
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.CorrectionDetailsStory
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Lamp
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Action as CorrectionDetailsAction
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Culture as CorrectionDetailsCulture

class MainStory : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, TAG) {
    companion object {
        const val TAG = "MainInteraction"

        fun addSpiritsToLamp(lamp: Lamp) {
            with(lamp) {
                add(ReadReportsDjinn)
                add(FetchStockMarketSamplesGenie)
                add(UpdateRebellionPricesGenie)
            }
        }
    }
}

sealed class Vision {

    data class Loading(
        val maybePortals: MainPortals?,
        val maybeRebellionBook: RebellionBook?
    ) : Vision()

    data class Viewing(
        val rebellionReport: RebellionReport,
        val isRefreshing: Boolean,
        val portals: MainPortals,
        val rebellionBook: RebellionBook
    ) : Vision()
}

fun start() = Vision.Loading(null, null) as Vision

@Suppress("UNUSED_PARAMETER")
fun isEnding(maybe: Any?) = false

sealed class Action {
    data class Start(val rebellionBook: RebellionBook, val portals: MainPortals) : Action()
    data class SetReport(val report: RebellionReport) : Action()
    object FindConstituent : Action()
    object OpenCashEditor : Action()
    data class OpenCorrectionDetails(val correction: Correction) : Action()
    object Refresh : Action()
    data class ReceiveMarketSamples(val result: StockMarket.Result) : Action()
    data class ReceiveError(val error: Throwable) : Action()
    object Ignore : Action()
}

data class MainPortals(
    val constituentSearchPortal: Portal<Unit>,
    val cashEditingPortal: Portal<Unit>
)

fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    return when {
        vision is Vision.Loading && action is Action.Start -> {
            val reportsWish = ReadReportsDjinn.wish(
                name = "rebellions",
                params = action.rebellionBook,
                resultToAction = { Action.SetReport(it) as Action },
                errorToAction = { throw IllegalStateException() }
            )
            Revision(Vision.Loading(action.portals, action.rebellionBook), reportsWish)
        }
        action is Action.SetReport -> {
            val portals = when (vision) {
                is Vision.Loading -> vision.maybePortals ?: error("No portals")
                is Vision.Viewing -> vision.portals
            }
            val rebellionBook = when (vision) {
                is Vision.Loading -> vision.maybeRebellionBook ?: error("No rebellionBook")
                is Vision.Viewing -> vision.rebellionBook
            }
            val isRefreshing = false
            val newVision = Vision.Viewing(action.report, isRefreshing, portals, rebellionBook)
            Revision(newVision)
        }
        vision is Vision.Viewing && action is Action.FindConstituent -> {
            vision.portals.constituentSearchPortal.jump(Unit)
            Revision(vision)
        }
        vision is Vision.Viewing && action is Action.OpenCashEditor -> {
            vision.portals.cashEditingPortal.jump(Unit)
            Revision(vision)
        }
        vision is Vision.Viewing && action is Action.OpenCorrectionDetails -> {
            val rebellion = vision.rebellionBook.value
            val fullInvestment =
                (rebellion.fullInvestment as? CashEquivalent.Amount)?.cashAmount ?: error("No investment amount")
            val details = CorrectionDetails(
                assetSymbol = action.correction.assetSymbol,
                holding = rebellion.holdings[action.correction.assetSymbol],
                targetValue = action.correction.targetValue(fullInvestment)
            )
            val detailsWish = edge.wish(
                name = "details",
                interaction = CorrectionDetailsStory(),
                startAction = CorrectionDetailsAction.Start(
                    CorrectionDetailsCulture(
                        CorrectionDetailsBook(details, vision.rebellionBook)
                    )
                ),
                endVisionToAction = { Action.Ignore as Action }
            )
            Revision(vision, detailsWish)
        }
        vision is Vision.Viewing && action is Action.Refresh -> {
            val newVision = Vision.Viewing(vision.rebellionReport, true, vision.portals, vision.rebellionBook)
            val symbols = vision.rebellionBook.value.combinedAssetSymbols.map(AssetSymbol::string)
            val samplesWish = FetchStockMarketSamplesGenie.wish(
                name = "refresh",
                params = FetchStockMarketSamples(symbols),
                resultToAction = Action::ReceiveMarketSamples,
                errorToAction = Action::ReceiveError
            )
            Revision(newVision, samplesWish)
        }
        vision is Vision.Viewing && action is Action.ReceiveMarketSamples -> {
            val newVision = Vision.Viewing(vision.rebellionReport, false, vision.portals, vision.rebellionBook)
            val result = action.result
            val rebellionBook = vision.rebellionBook
            val wish = UpdateRebellionPricesGenie.wish(
                name = "update-prices",
                params = UpdateRebellionPrices(rebellionBook, result),
                resultToAction = { Action.Ignore },
                errorToAction = Action::ReceiveError
            )
            Revision(newVision, wish)
        }
        vision is Vision.Viewing && action is Action.ReceiveError -> {
            Log.e(MainStory.TAG, action.error.localizedMessage, action.error)
            val newVision = Vision.Viewing(vision.rebellionReport, false, vision.portals, vision.rebellionBook)
            Revision(newVision)
        }
        action is Action.Ignore -> Revision(vision)
        else -> throw NotImplementedError("VISION $vision ACTION $action")
    }
}
