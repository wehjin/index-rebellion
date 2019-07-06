package com.rubyhuntersky.indexrebellion.data.techtonic.fixture

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import java.math.BigDecimal
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object Fixture {
    val TODAY = Date()

    const val TSLA_NAME = "Tesla, Inc."
    val TSLA_INSTRUMENT = InstrumentId("TSLA", InstrumentType.StockExchange)
    val TSLA_SHARE_PRICE = CashAmount(420)
    val TSLA_MACRO_PRICE = CashAmount(BigDecimal.valueOf(170000000) * BigDecimal.valueOf(TSLA_SHARE_PRICE.toDouble()))
    val TSLA_SAMPLE = InstrumentSample(
        TSLA_INSTRUMENT,
        TSLA_NAME,
        TSLA_SHARE_PRICE,
        TSLA_MACRO_PRICE,
        Date(1533635280000)
    )

    val DRIFT = DEFAULT_DRIFT
        .replaceSample(TSLA_SAMPLE)
        .replaceHolding(SpecificHolding(TSLA_INSTRUMENT, Custodian.Etrade, BigDecimal.TEN, TODAY))
}