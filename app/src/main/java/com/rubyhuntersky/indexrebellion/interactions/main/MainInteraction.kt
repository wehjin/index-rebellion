package com.rubyhuntersky.indexrebellion.interactions.main

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.SharePrice
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.data.report.Correction
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.data.report.RebellionReport
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.NotImplementedPortal
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
    data class Viewing(val rebellionReport: RebellionReport, val isRefreshing: Boolean) : Vision()
}

sealed class Action {
    object FindConstituent : Action()
    object OpenCashEditor : Action()
    data class OpenCorrectionDetails(val correction: Correction) : Action()
    object Refresh : Action()
}

class MainInteraction(
    private val rebellionBook: RebellionBook,
    private val correctionDetailPortal: Portal<CorrectionDetails>,
    private val constituentSearchPortal: Portal<Unit> = NotImplementedPortal(),
    private val cashEditingPortal: Portal<Unit> = NotImplementedPortal()
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
            is Vision.Viewing -> updateViewing(action, vision)
        }
    }

    private fun updateViewing(action: MainAction, vision: Vision.Viewing) {
        return when (action) {
            is Action.FindConstituent -> constituentSearchPortal.jump(Unit)
            is Action.OpenCashEditor -> cashEditingPortal.jump(Unit)
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
                    val samples = result.samples
                        .fold(mutableMapOf<AssetSymbol, StockSample>()) { map, sample ->
                            map[AssetSymbol(sample.symbol)] = sample
                            map
                        }
                    val date = Date()
                    val constituents = rebellionBook.value.index.constituents
                        .map { old ->
                            samples[old.assetSymbol]?.let {
                                Constituent(
                                    assetSymbol = old.assetSymbol,
                                    marketWeight = MarketWeight(it.marketCapitalization),
                                    sharePrice = SharePrice(CashAmount(it.sharePrice), date),
                                    ownedShares = old.ownedShares,
                                    isRemoved = old.isRemoved
                                )
                            } ?: old
                        }
                    rebellionBook.updateConstituents(constituents)
                }
            }
            .addTo(composite)
    }

    private fun openCorrectionDetails(correction: Correction) {
        val rebellion = rebellionBook.value
        val assetSymbol = correction.assetSymbol
        val constituent = rebellion.findConstituent(assetSymbol) ?: return
        val ownedShares = constituent.ownedShares
        val ownedValue = (constituent.cashEquivalent as? CashEquivalent.Amount)?.cashAmount ?: return
        val fullInvestment = (rebellion.fullInvestment as? CashEquivalent.Amount)?.cashAmount ?: return
        val targetValue = correction.targetValue(fullInvestment)
        val details = CorrectionDetails(
            assetSymbol,
            ownedShares,
            ownedValue,
            targetValue
        )
        correctionDetailPortal.jump(details)
    }

    private fun updateLoading() {}
}
