package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Index(val constituents: List<Constituent> = emptyList(), val memo: String = "") {

    @Transient
    val combinedMarketWeight: MarketWeight by lazy {
        constituents.map { it.marketWeight }.fold(MarketWeight.ZERO, MarketWeight::plus)
    }

    fun wihConstituents(constituents: List<Constituent>): Index {
        return Index(constituents, memo)
    }

    fun withConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Index {
        val newConstituents = constituents
            .associateBy(Constituent::assetSymbol)
            .toMutableMap()
            .also { it[Constituent(assetSymbol, marketWeight).assetSymbol] = Constituent(assetSymbol, marketWeight) }
            .values
            .toList()
        return Index(newConstituents, memo)
    }

    fun deleteConstituent(assetSymbol: AssetSymbol): Index {
        val newConstituents = constituents
            .associateBy(Constituent::assetSymbol)
            .toMutableMap()
            .also { it.remove(assetSymbol) }
            .values
            .toList()
        return Index(newConstituents, memo)
    }
}
