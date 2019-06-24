package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.toCashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class DriftTest {

    private val now = Date()
    private val teslaId = InstrumentId("TSLA", InstrumentType.StockExchange)
    private val teslaName = "Tesla, Inc."
    private val teslaSharePrice = 420.toCashAmount()
    private val teslaSample = InstrumentSample(
        instrumentId = teslaId,
        instrumentName = teslaName,
        sharePrice = teslaSharePrice,
        macroPrice = 50000000000.toCashAmount(),
        sampleDate = now
    )
    private val teslaSize = 100.toBigDecimal()
    private val teslaFullSize = teslaSize.times(2.toBigDecimal())
    private val teslaHolding1 = SpecificHolding(
        instrumentId = teslaId,
        custodian = Custodian.Etrade,
        size = teslaSize,
        lastModified = now
    )
    private val teslaHolding2 = SpecificHolding(
        instrumentId = teslaId,
        custodian = Custodian.Wallet,
        size = teslaSize,
        lastModified = now
    )
    private val teslaValue = teslaSharePrice.times(teslaFullSize.toDouble())

    private val bitcoinId = InstrumentId("BTC", InstrumentType.BlockChain)
    private val bitcoinName = "Bitcoin"
    private val bitcoinPrice = 8000.toCashAmount()
    private val bitcoinSize = 1.toBigDecimal()
    private val bitcoinValue = bitcoinPrice.times(bitcoinSize.toDouble())
    private val bitcoinSample = InstrumentSample(
        instrumentId = bitcoinId,
        instrumentName = bitcoinName,
        sharePrice = bitcoinPrice,
        macroPrice = 140000000000.toCashAmount(),
        sampleDate = now
    )
    private val bitcoinHolding = SpecificHolding(
        instrumentId = bitcoinId,
        custodian = Custodian.Robinhood,
        size = bitcoinSize,
        lastModified = now
    )

    @Test
    fun generalHoldings() {
        val drift = DEFAULT_DRIFT
            .replaceSample(teslaSample)
            .replaceHolding(teslaHolding1)
            .replaceHolding(teslaHolding2)
            .replaceSample(bitcoinSample)
            .replaceHolding(bitcoinHolding)
        val expectedHoldings = setOf(
            GeneralHolding(
                DOLLAR_ID,
                BigDecimal.ZERO,
                setOf(Custodian.Wallet),
                DOLLAR_NAME,
                CashAmount.ZERO,
                ZERO_DOLLAR_HOLDING_LAST_MODIFIED
            ),
            GeneralHolding(teslaId, teslaFullSize, setOf(Custodian.Wallet, Custodian.Etrade), teslaName, teslaValue, now),
            GeneralHolding(bitcoinId, bitcoinSize, setOf(Custodian.Robinhood), bitcoinName, bitcoinValue, now)
        )
        val holdings = drift.generalHoldings
        assertEquals(expectedHoldings, holdings)
    }
}