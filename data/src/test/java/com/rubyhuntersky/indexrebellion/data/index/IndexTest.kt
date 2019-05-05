package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.assets.SharePrice
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class IndexTest {
    @Test
    fun cashEquivalentIsZeroWhenIndexHasNoConstituents() {
        val index = Index(constituents = emptyList(), memo = "No constituents")
        assertEquals(CashEquivalent.ZERO, index.cashEquivalentOfAllConstituents)
    }

    @Test
    fun cashEquivalentIsUnknownWhenConstituentSharePriceIsUnknown() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = null,
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(constituent), memo = "Unknown share price")
        assertEquals(CashEquivalent.Unknown(), index.cashEquivalentOfAllConstituents)
    }

    @Test
    fun cashEquivalentIsSharePriceTimesShareCountWhenConstituentHasPrice() {
        val constituent = Constituent(
            marketWeight = MarketWeight.ZERO,
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = SharePrice(CashAmount.TEN, Date()),
            ownedShares = ShareCount.ONE
        )
        val index = Index(constituents = listOf(constituent), memo = "Known share price")
        assertEquals(CashEquivalent.TEN, index.cashEquivalentOfAllConstituents)
    }
}