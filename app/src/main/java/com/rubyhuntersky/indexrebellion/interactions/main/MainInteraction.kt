package com.rubyhuntersky.indexrebellion.interactions.main

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.data.report.Correction
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.data.report.RebellionReport
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.SubjectInteraction
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.stockcatalog.StockSample
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*


typealias MainVision = Vision
typealias MainAction = Action

sealed class Vision {
    object Loading : Vision()
    data class Viewing(
        val rebellionReport: RebellionReport,
        val isRefreshing: Boolean
    ) : Vision()
}

//fun start() = Vision.Loading as Vision
//
//fun isEnding(maybe: Any?) = false

//fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
//    return when {
//        vision is Vision.Loading && action is Action.Start -> {
//            val reports = action.book.reader
//                .subscribe {
//                    setVision(Vision.Viewing(RebellionReport(it), isRefreshing = false))
//                }
//            Revision()
//        }
//        else -> throw NotImplementedError()
//    }
//}

sealed class Action {
    //data class Start(val book: RebellionBook, val portals: MainPortals) : Action()
    //data class SetReport(val report: RebellionReport) : Action()

    object FindConstituent : Action()
    object OpenCashEditor : Action()
    data class OpenCorrectionDetails(val correction: Correction) : Action()
    object Refresh : Action()
}

data class MainPortals(
    val correctionDetailPortal: Portal<CorrectionDetails>,
    val constituentSearchPortal: Portal<Unit>,
    val cashEditingPortal: Portal<Unit>
)

class MainInteraction(
    private val rebellionBook: RebellionBook,
    private val portals: MainPortals
) : SubjectInteraction<MainVision, MainAction>(Vision.Loading) {

    private val composite = CompositeDisposable()

    init {
        rebellionBook.reader
            .subscribe {
                setVision(Vision.Viewing(RebellionReport(it), isRefreshing = false))
            }
            .addTo(composite)
    }

    override fun sendAction(action: MainAction) {
        val vision = this.vision
        when (vision) {
            is Vision.Loading -> updateLoading()
            is Vision.Viewing -> updateViewing(vision, action)
        }
    }

    private fun updateViewing(vision: Vision.Viewing, action: MainAction) {
        return when (action) {
            is Action.FindConstituent -> portals.constituentSearchPortal.jump(Unit)
            is Action.OpenCashEditor -> portals.cashEditingPortal.jump(Unit)
            is Action.OpenCorrectionDetails -> openCorrectionDetails(action.correction)
            Action.Refresh -> refreshConstituents(vision)
        }
    }

    private fun refreshConstituents(vision: Vision.Viewing) {
        setVision(Vision.Viewing(vision.rebellionReport, isRefreshing = true))
        StockMarket.fetchSamples(rebellionBook.symbols)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe { result ->
                (result as? StockMarket.Result.Samples)?.let {
                    val samples =
                        result.samples
                            .fold(mutableMapOf<AssetSymbol, StockSample>()) { map, sample ->
                                map.also { it[AssetSymbol(sample.symbol)] = sample }
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
                    rebellionBook.updateConstituents(constituents, holdings)

                }
            }
            .addTo(composite)
    }

    private fun openCorrectionDetails(correction: Correction) {
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

    private fun updateLoading() {}
}
