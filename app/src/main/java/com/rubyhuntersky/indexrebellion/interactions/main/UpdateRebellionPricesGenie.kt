package com.rubyhuntersky.indexrebellion.interactions.main

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import com.rubyhuntersky.indexrebellion.toMacroValue
import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.stockcatalog.StockSample
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

object UpdateRebellionPricesGenie :
    Genie<UpdateRebellionPrices, Unit> {

    override val paramsClass: Class<UpdateRebellionPrices> = UpdateRebellionPrices::class.java

    override fun toSingle(params: UpdateRebellionPrices): Single<Unit> = Completable.create {
        val (rebellionBook, stockMarketResult) = params
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
                        Constituent(
                            old.assetSymbol,
                            MarketWeight(it.toMacroValue())
                        )
                    } ?: old
                }
            val date = Date()
            val holdings = rebellionBook.value.holdings
                .map { (symbol, holding) ->
                    samples[symbol]?.let {
                        holding.withSharePrice(
                            PriceSample(
                                CashAmount(
                                    it.sharePrice
                                ), date
                            )
                        )
                    } ?: holding
                }
            val newRebellion = rebellionBook.value.withConstituentsAndHoldings(constituents, holdings)
            rebellionBook.write(newRebellion)
        }
    }.toSingleDefault(Unit)
}