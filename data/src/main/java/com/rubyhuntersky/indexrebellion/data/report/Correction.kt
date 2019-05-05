package com.rubyhuntersky.indexrebellion.data.report

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import kotlin.math.max

sealed class Correction {

    data class Hold(
        val holdAssetSymbol: AssetSymbol,
        val weight: Double
    ) : Correction() {
        override val targetWeight get() = weight
    }

    data class Buy(
        val buyAssetSymbol: AssetSymbol,
        override val targetWeight: Double,
        val actualWeight: Double,
        val deficit: CashAmount
    ) : Correction()

    data class Sell(
        val sellAssetSymbol: AssetSymbol,
        override val targetWeight: Double,
        val actualWeight: Double,
        val surplus: CashAmount
    ) : Correction()

    val assetSymbol: AssetSymbol
        get() {
            val correction = this
            return when (correction) {
                is Hold -> correction.holdAssetSymbol
                is Buy -> correction.buyAssetSymbol
                is Sell -> correction.sellAssetSymbol
            }
        }

    val highWeight: Double
        get() = when (this) {
            is Hold -> 0.0
            is Buy -> max(actualWeight, targetWeight)
            is Sell -> max(actualWeight, targetWeight)
        }

    abstract val targetWeight: Double

    fun targetValue(fullValue: CashAmount): CashAmount = fullValue * targetWeight
}