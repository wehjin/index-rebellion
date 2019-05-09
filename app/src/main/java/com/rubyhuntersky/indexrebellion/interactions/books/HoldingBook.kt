package com.rubyhuntersky.indexrebellion.interactions.books

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.interaction.core.Book
import io.reactivex.Observable

class RebellionHoldingBook(
    private val rebellionBook: RebellionBook,
    private val assetSymbol: AssetSymbol
) : Book<OwnedAsset>, HoldingBook {

    override val reader: Observable<OwnedAsset>
        get() = rebellionBook.reader
            .map { it.holdings[assetSymbol] ?: error("No holding with symbol : $assetSymbol") }
            .distinctUntilChanged()

    override fun write(value: OwnedAsset) = rebellionBook.updateHolding(value)

    override fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: PriceSample?,
        cashChange: CashAmount?
    ) = rebellionBook.updateShareCountPriceAndCash(assetSymbol, shareCount, sharePrice, cashChange)
}

interface HoldingBook : Book<OwnedAsset> {

    fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: PriceSample?,
        cashChange: CashAmount?
    )
}

