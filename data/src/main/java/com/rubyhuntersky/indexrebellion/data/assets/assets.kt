package com.rubyhuntersky.indexrebellion.data.assets

import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent


fun product(sharePrice: SharePrice, shareCount: ShareCount): CashEquivalent =
    if (shareCount == ShareCount.ZERO) {
        CashEquivalent.ZERO
    } else {
        sharePrice?.let {
            CashEquivalent.Amount(sharePrice.cashAmount * shareCount.value)
        } ?: CashEquivalent.Unknown()
    }

data class OwnedAsset(
    val assetSymbol: AssetSymbol,
    val shareCount: ShareCount,
    val sharePrice: SharePrice
)
