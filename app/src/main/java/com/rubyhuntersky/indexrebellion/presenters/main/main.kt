package com.rubyhuntersky.indexrebellion.presenters.main

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult
import java.util.*

fun RbhHoldingsResult.toHoldings(): List<OwnedAsset> {
    return positions.map { positionResult ->
        val instrumentLocation = positionResult.instrumentLocation
        val quote = quotesByInstrumentLocation[instrumentLocation]
            ?: error("No quote for instrument: $instrumentLocation")
        val assetSymbol = AssetSymbol(quote.symbol)
        val shareCount = ShareCount(positionResult.quantity)
        val sharePrice = PriceSample(CashAmount(quote.lastPrice), Date())
        OwnedAsset(assetSymbol, shareCount, sharePrice)
    }
}

data class CorrectionWeights(
    val leftSpace: Double, val leftWing: Double,
    val rightWing: Double, val rightSpecial: Double, val rightSpace: Double
)

object CorrectionWeightsCalculator {

    private const val maxWeight: Double = 1000.0
    private const val highWeight = maxWeight * 1.0

    fun calculate(highValue: Double, targetValue: Double, actualValue: Double): CorrectionWeights {
        require(highValue >= 0.0)
        require(targetValue in 0.0..highValue)
        require(actualValue in 0.0..highValue)
        return if (highValue == 0.0) {
            CorrectionWeights(
                leftSpace = maxWeight, leftWing = 0.0,
                rightWing = 0.0, rightSpecial = 0.0, rightSpace = maxWeight
            )
        } else {
            if (targetValue == actualValue) {
                val weight = (targetValue / highValue) * highWeight
                CorrectionWeights(
                    leftSpace = maxWeight - weight,
                    leftWing = weight,
                    rightWing = weight,
                    rightSpecial = 0.0,
                    rightSpace = maxWeight - weight
                )
            } else {
                val targetWeight = (targetValue / highValue) * highWeight
                val actualWeight = (actualValue / highValue) * highWeight
                if (targetValue > actualValue) {
                    CorrectionWeights(
                        leftSpace = maxWeight - targetWeight,
                        leftWing = targetWeight,
                        rightWing = actualWeight,
                        rightSpecial = (targetWeight - actualWeight),
                        rightSpace = maxWeight - targetWeight
                    )
                } else {
                    CorrectionWeights(
                        leftSpace = maxWeight - targetWeight,
                        leftWing = targetWeight,
                        rightWing = targetWeight,
                        rightSpecial = (actualWeight - targetWeight),
                        rightSpace = maxWeight - actualWeight
                    )
                }
            }
        }
    }
}