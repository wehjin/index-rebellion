package com.rubyhuntersky.indexrebellion.data.techtonic.market

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class InstrumentSample(
    val instrumentId: InstrumentId,
    val instrumentTitle: String,
    val sharePrice: CashAmount,
    val macroPrice: CashAmount,
    val sampleDate: Date
)