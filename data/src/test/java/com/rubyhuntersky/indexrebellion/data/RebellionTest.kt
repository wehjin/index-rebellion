package com.rubyhuntersky.indexrebellion.data

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.assets.SharePrice
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.Index
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

class RebellionTest {

    @Test
    fun seedRebellion() {
        assertNotNull(Rebellion.SEED)
    }

    @Test
    fun fullInvestmentEqualsNewInvestmentWhenIndexIsEmpty() {
        val emptyIndex = Index.EMPTY
        val newInvestments = listOf(CashAmount.ONE, CashAmount.TEN)
        val fullInvestments = newInvestments
            .map { newInvestment ->
                Rebellion(
                    index = emptyIndex,
                    newInvestment = newInvestment
                )
            }
            .map { rebellion -> rebellion.fullInvestment }

        assertEquals(listOf(CashEquivalent.ONE, CashEquivalent.TEN), fullInvestments)
    }

    @Test
    fun fullInvestmentIsUnknownWhenIndexIncludesUnpricedConstituent() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = null,
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(constituent), memo = "")
        val rebellion = Rebellion(index = index, newInvestment = CashAmount.TEN)
        assertEquals(CashEquivalent.Unknown(), rebellion.fullInvestment)
    }

    @Test
    fun fullInvestmentCombinesIndexCashEquivalentAndNewInvestment() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice(CashAmount.TEN, Date()),
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(constituent), memo = "")
        val rebellion = Rebellion(index = index, newInvestment = CashAmount.ONE)
        assertEquals(CashEquivalent.Amount(CashAmount(11)), rebellion.fullInvestment)
    }

    @Test
    fun serializable() {
        val rebellion = Rebellion.SEED.addConstituent(AssetSymbol("TWLO"), MarketWeight.TEN)
        val jsonData = Json.stringify(Rebellion.serializer(), rebellion)
        println(jsonData)
        val rebellionReborn = Json.parse(Rebellion.serializer(), jsonData)
        println(rebellionReborn)
        assertEquals(rebellion, rebellionReborn)
    }
}