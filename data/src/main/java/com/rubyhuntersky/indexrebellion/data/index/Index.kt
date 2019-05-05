package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.cash.sum
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
data class Index(val constituents: List<Constituent>, val memo: String) {

    @Transient
    val includedConstituents: List<Constituent> by lazy {
        constituents.filter { !it.isRemoved }
    }

    @Transient
    val includedOrOwnedConstituents: List<Constituent> by lazy {
        constituents.filter { !it.isRemoved || it.ownedShares > ShareCount.ZERO }
    }

    @Transient
    val totalMarketWeightOfIncludedConstituents: MarketWeight by lazy {
        includedConstituents.map { it.marketWeight }.fold(MarketWeight.ZERO, MarketWeight::plus)
    }

    @Transient
    val cashEquivalentOfAllConstituents: CashEquivalent by lazy {
        if (constituents.isEmpty()) {
            CashEquivalent.ZERO
        } else {
            constituents.map { it.cashEquivalent }.fold(CashEquivalent.ZERO, ::sum)
        }
    }

    @Transient
    val refreshDate: Date
        get() = constituents
            .map(Constituent::sharePrice)
            .map { sharePrice ->
                sharePrice?.date ?: Date(0)
            }
            .fold(Date(0)) { latest, next ->
                if (next.after(latest)) {
                    next
                } else {
                    latest
                }
            }

    fun updateConstituents(constituents: List<Constituent>): Index =
        Index(constituents, memo)

    fun updateConstituent(constituent: Constituent): Index =
        Index(constituents.updateConstituent(constituent), memo)

    fun addConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Index =
        Index(
            constituents = constituents.find { it.assetSymbol == assetSymbol }?.let {
                constituents.updateConstituent(it.reactivate(marketWeight))
            } ?: constituents.updateConstituent(
                Constituent(
                    assetSymbol,
                    marketWeight
                )
            ),
            memo = memo
        )

    private fun List<Constituent>.updateConstituent(constituent: Constituent): List<Constituent> {
        val mutable = toMutableList()
        find { it.assetSymbol == constituent.assetSymbol }?.let { mutable.remove(it) }
        mutable.add(constituent)
        return mutable
    }

    fun deleteConstituent(assetSymbol: AssetSymbol): Index {
        val newConstituents = constituents.map {
            if (it.assetSymbol == assetSymbol) {
                it.delete()
            } else {
                it
            }
        }
        return Index(newConstituents, memo)
    }

    companion object {
        val EMPTY = Index(emptyList(), "")
    }
}
