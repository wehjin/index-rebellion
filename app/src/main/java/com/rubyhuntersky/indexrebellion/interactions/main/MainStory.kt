package com.rubyhuntersky.indexrebellion.interactions.main

import android.util.Log
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.data.report.Correction
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.data.report.RebellionReport
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.stockcatalog.StockSample
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

const val MAIN_INTERACTION_TAG = "MainInteraction"

sealed class Vision {

    data class Loading(
        val maybePortals: MainPortals?,
        val maybeRebellionBook: Book<Rebellion>?
    ) : Vision()

    data class Viewing(
        val rebellionReport: RebellionReport,
        val isRefreshing: Boolean,
        val portals: MainPortals,
        val rebellionBook: Book<Rebellion>
    ) : Vision()
}

fun start() = Vision.Loading(null, null) as Vision

@Suppress("UNUSED_PARAMETER")
fun isEnding(maybe: Any?) = false

sealed class Action {
    data class Start(val rebellionBook: Book<Rebellion>, val portals: MainPortals) : Action()
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
    val correctionDetailPortal: Portal<CorrectionDetails>,
    val constituentSearchPortal: Portal<Unit>,
    val cashEditingPortal: Portal<Unit>
)

fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        vision is Vision.Loading && action is Action.Start -> {
            val reportsWish = action.rebellionBook.reader
                .toWish("rebellions",
                    onNext = { Action.SetReport(RebellionReport(it)) as Action },
                    onError = { throw IllegalStateException() }
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
            openCorrectionDetails(vision.rebellionBook, vision.portals, action.correction)
            Revision(vision)
        }
        vision is Vision.Viewing && action is Action.Refresh -> {
            val newVision = Vision.Viewing(vision.rebellionReport, true, vision.portals, vision.rebellionBook)
            val symbols = vision.rebellionBook.value.combinedAssetSymbols.map(AssetSymbol::string)
            val samplesWish = StockMarket.fetchSamples(symbols)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .toWish("refresh", Action::ReceiveMarketSamples, Action::ReceiveError)
            Revision(newVision, samplesWish)
        }
        vision is Vision.Viewing && action is Action.ReceiveMarketSamples -> {
            val newVision = Vision.Viewing(vision.rebellionReport, false, vision.portals, vision.rebellionBook)
            val result = action.result
            val rebellionBook = vision.rebellionBook
            val writePricesWish = updateRebellionPrices(rebellionBook, result).toWish(
                "update-prices",
                { Action.Ignore },
                Action::ReceiveError
            )
            Revision(newVision, writePricesWish)
        }
        vision is Vision.Viewing && action is Action.ReceiveError -> {
            Log.e(MAIN_INTERACTION_TAG, action.error.localizedMessage, action.error)
            val newVision = Vision.Viewing(vision.rebellionReport, false, vision.portals, vision.rebellionBook)
            Revision(newVision)
        }
        action is Action.Ignore -> Revision(vision)
        else -> throw NotImplementedError("VISION $vision ACTION $action")
    }
}


class MainStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, MAIN_INTERACTION_TAG)

private fun updateRebellionPrices(rebellionBook: Book<Rebellion>, stockMarketResult: StockMarket.Result): Single<Unit> {
    return Completable.create {
        (stockMarketResult as? StockMarket.Result.Samples)?.let {
            val samples = mutableMapOf<AssetSymbol, StockSample>()
            stockMarketResult.samples
                .fold(samples) { output, nextSample ->
                    output.also {
                        it[AssetSymbol(nextSample.symbol)] = nextSample
                    }
                }
            val constituents = rebellionBook.value.index.constituents
                .map { old ->
                    samples[old.assetSymbol]?.let {
                        Constituent(old.assetSymbol, MarketWeight(it.marketCapitalization))
                    } ?: old
                }
            val date = Date()
            val holdings = rebellionBook.value.holdings
                .map { (symbol, holding) ->
                    samples[symbol]?.let {
                        holding.withSharePrice(PriceSample(CashAmount(it.sharePrice), date))
                    } ?: holding
                }
            val newRebellion = rebellionBook.value.withConstituentsAndHoldings(constituents, holdings)
            rebellionBook.write(newRebellion)
        }
    }.toSingleDefault(Unit)
}

private fun openCorrectionDetails(rebellionBook: Book<Rebellion>, portals: MainPortals, correction: Correction) {
    val rebellion = rebellionBook.value
    val assetSymbol = correction.assetSymbol
    val holding = rebellion.holdings[assetSymbol] ?: return
    val ownedShares = holding.shareCount
    val ownedValue = (holding.cashEquivalent as? CashEquivalent.Amount)?.cashAmount ?: return
    val fullInvestment = (rebellion.fullInvestment as? CashEquivalent.Amount)?.cashAmount ?: return
    val targetValue = correction.targetValue(fullInvestment)
    val details = CorrectionDetails(assetSymbol, ownedShares, ownedValue, targetValue)
    portals.correctionDetailPortal.jump(details)
}
