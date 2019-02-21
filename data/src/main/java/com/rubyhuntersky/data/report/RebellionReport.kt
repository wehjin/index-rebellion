package com.rubyhuntersky.data.report

import com.rubyhuntersky.data.Rebellion
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.data.cash.CashEquivalent
import com.rubyhuntersky.data.index.MarketWeight
import java.util.*

data class RebellionReport(private val rebellion: Rebellion) {

    private val index = rebellion.index
    val currentInvestment: CashEquivalent by lazy { rebellion.index.cashEquivalentOfAllConstituents }
    val newInvestment: CashAmount get() = rebellion.newInvestment
    val fullInvestment: CashEquivalent get() = rebellion.fullInvestment

    val refreshDate: Date
        get() = rebellion.index.refreshDate

    data class Funding(
        val currentInvestment: CashEquivalent,
        val newInvestment: CashAmount,
        val fullInvestment: CashEquivalent
    )

    val funding: Funding = Funding(currentInvestment, newInvestment, fullInvestment)

    sealed class Conclusion {
        object AddConstituent : Conclusion()
        object RefreshPrices : Conclusion()
        data class Divest(val corrections: List<Correction>) : Conclusion()
        data class Maintain(val corrections: List<Correction>) : Conclusion()
    }

    val conclusion: Conclusion by lazy {
        val totalMarketWeight = index.totalMarketWeightOfIncludedConstituents
        if (totalMarketWeight == MarketWeight.ZERO) {
            Conclusion.AddConstituent
        } else {
            val fullInvestment = fullInvestment
            when (fullInvestment) {
                is CashEquivalent.Unknown -> Conclusion.RefreshPrices
                is CashEquivalent.Amount -> {
                    if (fullInvestment <= CashEquivalent.ZERO) {
                        val currentInvestment = currentInvestment as CashEquivalent.Amount
                        val constituentCorrections = index.includedOrOwnedConstituents
                            .map {
                                val actualWeight = if (currentInvestment <= 0) {
                                    0.0
                                } else {
                                    it.cashEquivalent as CashEquivalent.Amount / currentInvestment
                                }
                                if (actualWeight == 0.0) {
                                    Correction.Hold(
                                        holdAssetSymbol = it.assetSymbol,
                                        weight = actualWeight
                                    )
                                } else {
                                    Correction.Sell(
                                        sellAssetSymbol = it.assetSymbol,
                                        targetWeight = 0.0,
                                        actualWeight = actualWeight,
                                        surplus = it.cashEquivalent.toCashAmount()
                                    )
                                }
                            }
                        Conclusion.Divest(constituentCorrections)
                    } else {
                        val constituentCorrections = index.includedOrOwnedConstituents
                            .map {
                                val targetWeight = if (it.isRemoved) 0.0 else (it.marketWeight / totalMarketWeight)
                                val actualWeight = it.cashEquivalent as CashEquivalent.Amount / fullInvestment
                                when {
                                    actualWeight == targetWeight -> Correction.Hold(
                                        holdAssetSymbol = it.assetSymbol,
                                        weight = targetWeight
                                    )
                                    actualWeight < targetWeight -> Correction.Buy(
                                        buyAssetSymbol = it.assetSymbol,
                                        targetWeight = targetWeight,
                                        actualWeight = actualWeight,
                                        deficit = fullInvestment.cashAmount * (targetWeight - actualWeight)
                                    )
                                    else -> Correction.Sell(
                                        sellAssetSymbol = it.assetSymbol,
                                        targetWeight = targetWeight,
                                        actualWeight = actualWeight,
                                        surplus = fullInvestment.cashAmount * (actualWeight - targetWeight)
                                    )
                                }
                            }
                        Conclusion.Maintain(constituentCorrections)
                    }
                }
            }
        }
    }

}