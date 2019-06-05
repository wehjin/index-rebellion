package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import java.math.BigDecimal
import java.util.*

data class GeneralHolding(
    val instrumentId: InstrumentId,
    val size: BigDecimal,
    val custodians: Set<Custodian>,
    val instrumentName: String?,
    val cashValue: CashAmount?,
    val lastModified: Date
)
