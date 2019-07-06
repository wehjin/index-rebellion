package com.rubyhuntersky.indexrebellion.data

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.Index
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

class RebellionTest {

    private val tsla = AssetSymbol("TSLA")

    @Test
    fun seedRebellion() {
        assertNotNull(Rebellion())
    }

    @Test
    fun cashEquivalentIsZeroWhenHoldingsAreEmpty() {
        val rebellion = Rebellion(index = Index(), newInvestment = CashAmount.ONE, holdings = emptyMap())
        assertEquals(CashEquivalent.ZERO, rebellion.cashEquivalentOfHoldings)
    }

    @Test
    fun fullInvestmentEqualsNewInvestmentWhenHoldingsIsEmpty() {
        val newInvestments = listOf(CashAmount.ONE, CashAmount.TEN)
        val fullInvestments = newInvestments
            .map { Rebellion(index = Index(), newInvestment = it, holdings = emptyMap()) }
            .map { rebellion -> rebellion.fullInvestment }
        assertEquals(listOf(CashEquivalent.ONE, CashEquivalent.TEN), fullInvestments)
    }

    @Test
    fun fullInvestmentCombinesIndexCashEquivalentAndNewInvestment() {
        val constituent = Constituent(assetSymbol = tsla, marketWeight = MarketWeight.ZERO)
        val index = Index(constituents = listOf(constituent), memo = "")
        val newInvestment = CashAmount.ONE
        val holdings = mapOf(Pair(tsla, OwnedAsset(tsla, ShareCount.ONE, PriceSample(CashAmount.TEN, Date()))))
        val rebellion = Rebellion(index, newInvestment, holdings)
        assertEquals(CashEquivalent.Amount(CashAmount(11)), rebellion.fullInvestment)
    }

    @UnstableDefault
    @Test
    fun serializable() {
        val rebellion = Rebellion().withConstituent(AssetSymbol("TWLO"), MarketWeight.TEN)
        val jsonData = Json.stringify(Rebellion.serializer(), rebellion)
        println(jsonData)
        val rebellionReborn = Json.parse(Rebellion.serializer(), jsonData)
        println(rebellionReborn)
        assertEquals(rebellion, rebellionReborn)
    }
}