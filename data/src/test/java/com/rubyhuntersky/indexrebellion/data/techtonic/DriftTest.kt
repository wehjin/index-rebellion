package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.cash.toCashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import org.junit.Test
import java.util.*

class DriftTest {

    private val now = Date()
    private val teslaId = InstrumentId("TSLA", InstrumentType.StockExchange)
    private val teslaSample = InstrumentSample(
        instrumentId = teslaId,
        instrumentTitle = "Tesla, Inc.",
        sharePrice = 420.toCashAmount(),
        macroPrice = 50000000000.toCashAmount(),
        sampleDate = now
    )
    private val teslaHolding1 = SpecificHolding(
        instrumentId = teslaId,
        custodian = Custodian.Etrade,
        size = 100.toBigDecimal(),
        lastModified = now
    )
    private val teslaHolding2 = SpecificHolding(
        instrumentId = teslaId,
        custodian = Custodian.None,
        size = 100.toBigDecimal(),
        lastModified = now
    )

    private val bitcoinId = InstrumentId("BTC", InstrumentType.BlockChain)
    private val bitcoinSample = InstrumentSample(
        instrumentId = bitcoinId,
        instrumentTitle = "Bitcoin",
        sharePrice = 8000.toCashAmount(),
        macroPrice = 140000000000.toCashAmount(),
        sampleDate = now
    )
    private val bitcoinHolding = SpecificHolding(
        instrumentId = bitcoinId,
        custodian = Custodian.Robinhood,
        size = 1.toBigDecimal(),
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
    }
}