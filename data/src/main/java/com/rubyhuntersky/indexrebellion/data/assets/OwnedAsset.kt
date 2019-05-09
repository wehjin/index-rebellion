package com.rubyhuntersky.indexrebellion.data.assets

import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class OwnedAsset(
    val assetSymbol: AssetSymbol,
    val shareCount: ShareCount,
    val sharePrice: PriceSample
) {
    @Transient
    val cashEquivalent: CashEquivalent
        get() = shareCount * sharePrice

    fun withSharePrice(newSharePrice: PriceSample): OwnedAsset = OwnedAsset(assetSymbol, shareCount, newSharePrice)
}
