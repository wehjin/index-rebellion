package com.rubyhuntersky.indexrebellion.data.report

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.fractionOfTotal
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import java.util.*

data class RebellionReport(private val rebellion: Rebellion) {

    private val index = rebellion.index
    val currentInvestment: CashEquivalent by lazy { rebellion.cashEquivalentOfHoldings }
    val newInvestment: CashAmount get() = rebellion.newInvestment
    val fullInvestment: CashEquivalent get() = rebellion.fullInvestment
    val refreshDate: Date get() = rebellion.refreshDate

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
        val totalMarketWeight = index.combinedMarketWeight
        if (totalMarketWeight == MarketWeight.ZERO) {
            Conclusion.AddConstituent
        } else {
            when (val fullInvestment = fullInvestment) {
                is CashEquivalent.Unknown ->
                    Conclusion.RefreshPrices
                is CashEquivalent.Amount -> {
                    if (fullInvestment <= CashEquivalent.ZERO) {
                        val currentInvestment = currentInvestment as CashEquivalent.Amount
                        val constituentCorrections = rebellion.combinedAssetSymbols
                            .map { assetSymbol ->
                                val holding = rebellion.holdings[assetSymbol]
                                val actualWeight = holding.fractionOfTotal(currentInvestment)
                                if (actualWeight == 0.0) {
                                    Correction.Hold(assetSymbol, weight = actualWeight)
                                } else {
                                    val targetWeight = 0.0
                                    Correction.Sell(
                                        assetSymbol, targetWeight, actualWeight,
                                        surplus = holding!!.cashEquivalent.toCashAmount()
                                    )
                                }
                            }
                        Conclusion.Divest(constituentCorrections)
                    } else {
                        val constituentCorrections = rebellion.combinedAssetSymbols
                            .map { assetSymbol ->
                                val constituent = index.constituents.find { it.assetSymbol == assetSymbol }
                                val targetWeight = constituent?.let { it.marketWeight / totalMarketWeight } ?: 0.0
                                val actualWeight = rebellion.holdings[assetSymbol].fractionOfTotal(fullInvestment)
                                when {
                                    actualWeight == targetWeight ->
                                        Correction.Hold(assetSymbol, weight = targetWeight)
                                    actualWeight < targetWeight ->
                                        Correction.Buy(
                                            assetSymbol, targetWeight, actualWeight,
                                            deficit = fullInvestment.cashAmount * (targetWeight - actualWeight)
                                        )
                                    else ->
                                        Correction.Sell(
                                            assetSymbol, targetWeight, actualWeight,
                                            surplus = fullInvestment.cashAmount * (actualWeight - targetWeight)
                                        )
                                }
                            }
                        Conclusion.Maintain(
                            constituentCorrections
                        )
                    }
                }
            }
        }
    }

}