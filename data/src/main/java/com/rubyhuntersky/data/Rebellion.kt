package com.rubyhuntersky.data

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.Constituent
import com.rubyhuntersky.data.index.Index
import com.rubyhuntersky.data.index.MarketWeight
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Rebellion(val index: Index, val newInvestment: CashAmount) {

    @Transient
    val fullInvestment: CashEquivalent
        get() = index.cashEquivalentOfAllConstituents + newInvestment

    fun addConstituent(assetSymbol: AssetSymbol, marketWeight: MarketWeight): Rebellion =
        Rebellion(index.addConstituent(assetSymbol, marketWeight), newInvestment)

    fun updateConstituent(constituent: Constituent) = Rebellion(
        index = index.updateConstituent(constituent),
        newInvestment = newInvestment
    )

    fun updateConstituentAndCash(constituent: Constituent, cashChange: CashAmount?) = Rebellion(
        index = index.updateConstituent(constituent),
        newInvestment = cashChange?.let { newInvestment + it } ?: newInvestment
    )

    fun setNewInvestment(cashAmount: CashAmount): Rebellion = Rebellion(index, cashAmount)

    companion object {
        val SEED = Rebellion(Index.EMPTY, CashAmount.ZERO)
    }
}
