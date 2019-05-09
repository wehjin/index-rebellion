package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import org.junit.Assert.assertEquals
import org.junit.Test

class IndexTest {

    @Test
    fun combinedMarketWeightSumsWeightsOfConstituents() {
        val index = Index(
            listOf(
                Constituent(AssetSymbol("TSLA"), MarketWeight.ZERO),
                Constituent(AssetSymbol("AMZN"), MarketWeight.ONE)
            )
        )
        assertEquals(MarketWeight.ONE, index.combinedMarketWeight)
    }
}