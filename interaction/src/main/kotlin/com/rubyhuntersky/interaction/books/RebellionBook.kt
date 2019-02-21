package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.assets.ShareCount
import com.rubyhuntersky.data.assets.SharePrice
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.index.Constituent
import com.rubyhuntersky.interaction.core.Book
import io.reactivex.Observable

interface RebellionBook : Book<Rebellion> {

    fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: SharePrice,
        cashChange: CashAmount?
    ) {
        val rebellion = value
        val constituent = rebellion.index.constituents.firstOrNull {
            it.assetSymbol == assetSymbol
        }
        constituent?.let {
            val updatedConstituent = Constituent(assetSymbol, it.marketWeight, sharePrice, shareCount, it.isRemoved)
            val updatedRebellion = rebellion.updateConstituentAndCash(updatedConstituent, cashChange)
            write(updatedRebellion)
        }
    }

    val symbols: List<String>
        get() = value.index.constituents.map(Constituent::assetSymbol).map(AssetSymbol::string)

    fun updateConstituent(constituent: Constituent) =
        write(value.updateConstituent(constituent))

    fun constituentReader(assetSymbol: AssetSymbol): Observable<Constituent> {
        return reader.switchMap { rebellion ->
            val constituent = rebellion.index.constituents.find { it.assetSymbol == assetSymbol }
            if (constituent != null) {
                Observable.just(constituent)
            } else {
                Observable.never()
            }
        }
    }

    fun updateConstituents(constituents: List<Constituent>) =
        write(value.updateConstituents(constituents))
}


