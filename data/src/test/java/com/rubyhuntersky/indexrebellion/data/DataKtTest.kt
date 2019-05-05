package com.rubyhuntersky.indexrebellion.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DataKtTest {
    @Test
    fun doubleToStatConvertsValuesLessThanZero() {
        val result = .12345.toStatString()
        assertEquals("0", result)
    }

    @Test
    fun doubleToStatDisplaysExactlyThreeDigits() {
        val inputs = listOf(1.2345, 12.345, 123.45, 1234.5)
        val result = inputs.map(Double::toStatString)
        assertEquals(listOf("1.23", "12.3", "123", "1.23K"), result)
    }

    @Test
    fun doubleToStatDisplaysMultiplier() {
        val inputs = listOf(
            3.1415926535897932384626433832,
            3141.5926535897932384626433832,
            3141592.6535897932384626433832,
            3141592653.5897932384626433832,
            3141592653589.7932384626433832,
            3141592653589793.2384626433832,
            3141592653589793238.4626433832,
            3141592653589793238462.6433832,
            3141592653589793238462643.3832,
            3141592653589793238462643383.2
        )
        val result = inputs.map(Double::toStatString)
        assertEquals(
            listOf(
                "3.14",
                "3.14K",
                "3.14M",
                "3.14B",
                "3.14T",
                "3.14P",
                "3.14E",
                "3.14Z",
                "3.14Y",
                "3.14e9"
            ), result
        )
    }
}