package com.rubyhuntersky.indexrebellion.interactions.books

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.assets.SharePrice
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.interaction.core.Book
import io.reactivex.Observable

class RebellionConstituentBook(private val rebellionBook: RebellionBook, private val assetSymbol: AssetSymbol) :
    Book<Constituent>, ConstituentBook {

    override val reader: Observable<Constituent>
        get() = rebellionBook.reader
            .map { rebellion ->
                rebellion.index.constituents
                    .find {
                        it.assetSymbol == assetSymbol
                    }!!
            }
            .distinctUntilChanged()

    override fun write(value: Constituent) {
        rebellionBook.updateConstituent(value)
    }

    override fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: SharePrice,
        cashChange: CashAmount?
    ) = rebellionBook.updateShareCountPriceAndCash(assetSymbol, shareCount, sharePrice, cashChange)
}

interface ConstituentBook : Book<Constituent> {

    fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: SharePrice,
        cashChange: CashAmount?
    )
}

