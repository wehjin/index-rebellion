package com.rubyhuntersky.indexrebellion.spirits.genies

import com.rubyhuntersky.interaction.GenieParams2
import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.stockcatalog.StockMarket
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

data class RefreshStocks(val symbols: List<String>) :
    GenieParams2<StockMarket.Result, RefreshStocks> {

    object GENIE : Genie<RefreshStocks, StockMarket.Result> {
        override val paramsClass: Class<RefreshStocks> = RefreshStocks::class.java

        override fun toSingle(params: RefreshStocks): Single<StockMarket.Result> {
            return StockMarket
                .fetchSamples(params.symbols)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
        }
    }
}
