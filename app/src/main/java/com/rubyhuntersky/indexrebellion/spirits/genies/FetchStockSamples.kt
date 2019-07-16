package com.rubyhuntersky.indexrebellion.spirits.genies

import com.rubyhuntersky.interaction.GenieParams2
import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.stockcatalog.StockMarket
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

data class FetchStockSamples(val symbols: List<String>) :
    GenieParams2<FetchStockSamples, StockMarket.Result> {

    object GENIE : Genie<FetchStockSamples, StockMarket.Result> {
        override val paramsClass: Class<FetchStockSamples> = FetchStockSamples::class.java

        override fun toSingle(params: FetchStockSamples): Single<StockMarket.Result> {
            return StockMarket
                .fetchSamples(params.symbols)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
        }
    }
}
