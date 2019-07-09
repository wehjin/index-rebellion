package com.rubyhuntersky.indexrebellion.projections.drift

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import java.math.BigDecimal

internal data class HoldingSight(
    val instrumentId: InstrumentId,
    val name: String,
    val custodians: List<String>,
    val count: BigDecimal,
    val symbol: String,
    val value: BigDecimal
)