package com.rubyhuntersky.indexrebellion.data

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.cash.sum
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.Index
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
data class Rebellion(
    val index: Index = Index(),
    val newInvestment: CashAmount = CashAmount.ZERO,
    val holdings: Map<AssetSymbol, OwnedAsset> = emptyMap()
) {
    @Transient
    val fullInvestment: CashEquivalent
        get() = cashEquivalentOfHoldings + newInvestment

    @Transient
    val cashEquivalentOfHoldings: CashEquivalent by lazy {
        if (holdings.isEmpty()) {
            CashEquivalent.ZERO
        } else {
            holdings.values.map { it.cashEquivalent }.fold(CashEquivalent.ZERO, ::sum)
        }
    }

    @Transient
    val combinedAssetSymbols: List<AssetSymbol> by lazy {
        holdings.keys.union(index.constituents.map(Constituent::assetSymbol)).toList()
    }

    @Transient
    val refreshDate: Date
        get() = holdings.values
            .map(OwnedAsset::sharePrice)
            .map(PriceSample::date)
            .fold(Date(0)) { latest, date -> if (date.after(latest)) date else latest }

    fun withConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Rebellion {
        return Rebellion(index.withConstituent(assetSymbol, marketWeight), newInvestment, holdings)
    }

    fun withHolding(holding: OwnedAsset): Rebellion {
        val newHoldings = holdings
            .toMutableMap()
            .also { it[holding.assetSymbol] = holding }
        return Rebellion(index, newInvestment, newHoldings)
    }

    fun withHoldings(holdings: List<OwnedAsset>) =
        Rebellion(index, newInvestment, holdings.associateBy { it.assetSymbol })

    fun setNewInvestment(cashAmount: CashAmount): Rebellion {
        return Rebellion(index, cashAmount, holdings)
    }

    fun deleteConstituent(assetSymbol: AssetSymbol): Rebellion {
        val newIndex = index.deleteConstituent(assetSymbol)
        return Rebellion(newIndex, newInvestment, holdings)
    }

    fun withConstituentsAndHoldings(constituents: List<Constituent>, holdings: List<OwnedAsset>): Rebellion {
        val newIndex = index.wihConstituents(constituents)
        val newHoldings = holdings.associateBy(OwnedAsset::assetSymbol)
        return Rebellion(newIndex, newInvestment, newHoldings)
    }
}
