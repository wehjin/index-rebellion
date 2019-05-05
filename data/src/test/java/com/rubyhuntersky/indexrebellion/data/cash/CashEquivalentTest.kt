package com.rubyhuntersky.indexrebellion.data.cash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CashEquivalentTest {

    @Test
    fun toDoubleConvertsUnknownToNull() {
        val result = CashEquivalent.Unknown().toDouble()
        assertNull(result)
    }

    @Test
    fun toDoubleConvertsAmountToDouble() {
        val result = CashEquivalent.Amount(10).toDouble()
        assertEquals(10.0, result)
    }
}