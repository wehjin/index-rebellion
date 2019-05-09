package com.rubyhuntersky.indexrebellion.data.assets

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class OwnedAssetTest {

    @Test
    fun cashEquivalentIsSharePriceTimesShareCount() {
        val asset = OwnedAsset(
            assetSymbol = AssetSymbol("TSLA"),
            sharePrice = PriceSample(CashAmount.TEN, Date()),
            shareCount = ShareCount.ONE
        )
        assertEquals(CashEquivalent.TEN, asset.cashEquivalent)
    }
}