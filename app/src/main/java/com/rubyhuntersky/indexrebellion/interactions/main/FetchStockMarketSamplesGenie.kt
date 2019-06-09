package com.rubyhuntersky.indexrebellion.interactions.main

import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.stockcatalog.StockMarket
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object FetchStockMarketSamplesGenie : Genie<FetchStockMarketSamples, StockMarket.Result> {

    override val paramsClass: Class<FetchStockMarketSamples> = FetchStockMarketSamples::class.java

    override fun toSingle(params: FetchStockMarketSamples): Single<StockMarket.Result> =
        StockMarket.fetchSamples(params.symbols)
            .subscribeOn(Schedulers.io()).observeOn(Schedulers.single())
}