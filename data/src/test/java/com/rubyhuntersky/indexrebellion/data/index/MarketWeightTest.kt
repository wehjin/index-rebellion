package com.rubyhuntersky.indexrebellion.data.index

import org.junit.Assert.assertEquals
import org.junit.Test

class MarketWeightTest {

    @Test
    fun buildFromDouble() {
        val marketWeight = MarketWeight(2.0)
        assertEquals(2.0, marketWeight.toDouble(), 0.1)
    }

    @Test
    fun constants() {
        val marketWeights = arrayOf(MarketWeight.ZERO, MarketWeight.ONE, MarketWeight.TEN)
        val expected = arrayOf(0.0, 1.0, 10.0)
        for (i in 0 until expected.size) {
            assertEquals(expected[i], marketWeights[i].toDouble(), 0.1)
        }
    }
}