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
import java.time.Month
import java.util.*

val DOLLAR_ID = InstrumentId("USD", InstrumentType.Fiat)

val UNIT_DOLLAR_SAMPLE = InstrumentSample(
    instrumentId = DOLLAR_ID,
    instrumentTitle = "US Dollars",
    sharePrice = CashAmount.ONE,
    macroPrice = CashAmount(BigDecimal("3286668000000")),
    sampleDate = GregorianCalendar(1933, Month.JUNE.ordinal, 5).time
)

val ZERO_DOLLAR_HOLDING_LAST_MODIFIED: Date = GregorianCalendar(2019, Month.JANUARY.ordinal, 1).time

val ZERO_DOLLAR_HOLDING = SpecificHolding(
    instrumentId = DOLLAR_ID,
    custodian = Custodian.None,
    size = BigDecimal.ZERO,
    lastModified = ZERO_DOLLAR_HOLDING_LAST_MODIFIED
)

val DOLLAR_FIAT_PLATING = InstrumentPlating(
    instrumentId = DOLLAR_ID,
    plate = Plate.Fiat
)

