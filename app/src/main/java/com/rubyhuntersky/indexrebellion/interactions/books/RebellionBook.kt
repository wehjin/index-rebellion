package com.rubyhuntersky.indexrebellion.interactions.books

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.interaction.core.Book

interface RebellionBook : Book<Rebellion> {

    fun updateShareCountPriceAndCash(
        assetSymbol: AssetSymbol,
        shareCount: ShareCount,
        sharePrice: PriceSample?,
        cashChange: CashAmount?
    ) {
        val newHolding = OwnedAsset(assetSymbol, shareCount, sharePrice!!)
        val rebellion = value
        val holding = rebellion.holdings[assetSymbol]
        if (holding == null || holding != newHolding) {
            val newRebellion = rebellion.withHolding(newHolding)
            write(newRebellion)
        }
    }

    val symbols: List<String>
        get() = value.combinedAssetSymbols.map(AssetSymbol::string)

    fun updateHolding(holding: OwnedAsset) =
        write(value.withHolding(holding))

    fun deleteConstituent(assetSymbol: AssetSymbol) =
        write(value.deleteConstituent(assetSymbol))

    fun updateConstituents(constituents: List<Constituent>, holdings: List<OwnedAsset>) =
        write(value.withConstituentsAndHoldings(constituents, holdings))
}


