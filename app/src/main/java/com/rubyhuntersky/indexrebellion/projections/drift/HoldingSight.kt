package com.rubyhuntersky.indexrebellion.projections.drift

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import java.math.BigDecimal

internal data class HoldingSight(
    val instrumentId: InstrumentId,
    val name: String,
    val custodians: List<String>,
    val count: BigDecimal,
    val symbol: String,
    val value: BigDecimal,
    val plate: Plate
)