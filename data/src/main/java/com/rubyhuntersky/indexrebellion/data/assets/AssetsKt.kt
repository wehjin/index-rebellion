package com.rubyhuntersky.indexrebellion.data.assets

import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent


fun product(sharePrice: PriceSample?, shareCount: ShareCount): CashEquivalent =
    if (shareCount == ShareCount.ZERO) {
        CashEquivalent.ZERO
    } else {
        sharePrice?.let {
            CashEquivalent.Amount(sharePrice.cashAmount * shareCount.value)
        } ?: CashEquivalent.Unknown()
    }

fun OwnedAsset?.fractionOfTotal(totalAmount: CashEquivalent.Amount): Double {
    return when {
        totalAmount <= 0 || this == null -> 0.0
        else -> cashEquivalent as CashEquivalent.Amount / totalAmount
    }
}
