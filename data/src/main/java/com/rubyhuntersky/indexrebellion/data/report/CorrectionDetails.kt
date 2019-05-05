package com.rubyhuntersky.indexrebellion.data.report

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount

data class CorrectionDetails(
    val assetSymbol: AssetSymbol,
    val ownedCount: ShareCount,
    val ownedValue: CashAmount,
    val targetValue: CashAmount
)