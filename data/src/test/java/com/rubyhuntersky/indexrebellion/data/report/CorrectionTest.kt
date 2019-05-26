package com.rubyhuntersky.indexrebellion.data.report

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import org.junit.Assert.assertEquals
import org.junit.Test

class CorrectionTest {

    @Test
    fun highWeight() {
        val corrections = listOf(
            Correction.Hold(AssetSymbol("A"), 0.5),
            Correction.Buy(AssetSymbol("B"), 1.0, 0.5, CashAmount.ZERO),
            Correction.Sell(AssetSymbol("C"), 0.5, 1.0, CashAmount.ZERO)
        )
        val highWeights = corrections.map(Correction::highWeight)
        assertEquals(listOf(0.5, 1.0, 1.0), highWeights)
    }
}