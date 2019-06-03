package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class SpecificHolding(
    val instrumentId: InstrumentId,
    val custodian: Custodian,
    val size: BigDecimal
)