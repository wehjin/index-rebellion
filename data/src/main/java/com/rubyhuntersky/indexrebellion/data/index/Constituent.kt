package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.assets.SharePrice
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Constituent constructor(
    val assetSymbol: AssetSymbol,
    val marketWeight: MarketWeight,
    val sharePrice: SharePrice = null,
    val ownedShares: ShareCount = ShareCount(0.0),
    val isRemoved: Boolean = false
) {

    @Transient
    val cashEquivalent: CashEquivalent
        get() = ownedShares * sharePrice

    fun reactivate(marketWeight: MarketWeight?): Constituent =
        Constituent(
            assetSymbol = assetSymbol,
            marketWeight = marketWeight ?: this.marketWeight,
            sharePrice = sharePrice,
            ownedShares = ownedShares,
            isRemoved = false
        )

    fun delete() =
        Constituent(
            assetSymbol,
            marketWeight,
            sharePrice,
            ownedShares,
            true
        )
}
