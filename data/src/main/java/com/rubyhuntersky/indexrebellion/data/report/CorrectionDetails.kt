package com.rubyhuntersky.indexrebellion.data.report

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent

data class CorrectionDetails(
    val assetSymbol: AssetSymbol,
    val holding: OwnedAsset?,
    val targetValue: CashAmount
) {

    val ownedValue
        get() = (holding?.cashEquivalent ?: CashEquivalent.ZERO).toCashAmount()

    val ownedCount
        get() = holding?.shareCount ?: ShareCount.ZERO
}