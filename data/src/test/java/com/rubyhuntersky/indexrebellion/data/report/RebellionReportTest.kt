package com.rubyhuntersky.indexrebellion.data.report

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.index.Constituent
import com.rubyhuntersky.indexrebellion.data.index.Index
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class RebellionReportTest {

    private val assetSymbol = AssetSymbol("TSLA")
    private val holdings = mapOf(
        Pair(
            assetSymbol,
            OwnedAsset(assetSymbol, ShareCount.TEN, PriceSample(cashAmount = CashAmount.TEN, date = Date()))
        )
    )
    private val index = Index(listOf(Constituent(assetSymbol, MarketWeight.ZERO)))
    private val newInvestment = CashAmount.TEN
    private val rebellion = Rebellion(index, newInvestment, holdings)

    @Test
    fun currentInvestmentIsHoldingsCashEquivalent() {
        val report = RebellionReport(rebellion)
        assertEquals(rebellion.cashEquivalentOfHoldings, report.currentInvestment)
    }

    @Test
    fun newInvestmentIsPublic() {
        val rebellion = Rebellion(index = Index(), newInvestment = CashAmount.TEN)
        val report = RebellionReport(rebellion)
        assertEquals(CashAmount.TEN, report.newInvestment)
    }

    @Test
    fun fullInvestmentIsIndexCashEquivalentPlusNewInvestment() {
        val report = RebellionReport(rebellion)
        assertEquals(rebellion.cashEquivalentOfHoldings + newInvestment, report.fullInvestment)
    }

    @Test
    fun marketWeightIsDividedAmongConstituentsInReport() {
        val constituent1 = Constituent(
            marketWeight = MarketWeight(55186399232),
            assetSymbol = AssetSymbol("TSLA")
        )
        val constituent2 = Constituent(
            marketWeight = MarketWeight(11258835968),
            assetSymbol = AssetSymbol("TWLO")
        )
        val newInvestment = CashAmount(10000.0)
        val index = Index(listOf(constituent1, constituent2), "")
        val rebellion = Rebellion(index, newInvestment)
        val expectedTargetWeights = listOf(0.8305546525027576, 0.16944534749724235)

        val rebellionReport = RebellionReport(rebellion)
        val conclusion = rebellionReport.conclusion
        val maintain = conclusion as RebellionReport.Conclusion.Maintain
        assertEquals(expectedTargetWeights, maintain.corrections.map { (it as Correction.Buy).targetWeight })
    }
}