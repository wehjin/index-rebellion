package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.InstrumentPlating
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import java.math.BigDecimal
import java.util.*

val DOLLAR_ID = InstrumentId("USD", InstrumentType.Fiat)
const val DOLLAR_NAME = "US Dollars"

val UNIT_DOLLAR_SAMPLE = InstrumentSample(
    instrumentId = DOLLAR_ID,
    instrumentName = DOLLAR_NAME,
    sharePrice = CashAmount.ONE,
    macroPrice = CashAmount(BigDecimal("3286668000000")),
    sampleDate = Calendar.getInstance().let { it.set(1933, 5, 5); it.time }
)

val ZERO_DOLLAR_HOLDING_LAST_MODIFIED: Date = Calendar.getInstance().let { it.set(2019, 0, 1); it.time }

val ZERO_DOLLAR_HOLDING = SpecificHolding(
    instrumentId = DOLLAR_ID,
    custodian = Custodian.Wallet,
    size = BigDecimal.ZERO,
    lastModified = ZERO_DOLLAR_HOLDING_LAST_MODIFIED
)

val DOLLAR_FIAT_PLATING = InstrumentPlating(
    instrumentId = DOLLAR_ID,
    plate = Plate.Fiat
)

