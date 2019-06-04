package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.*

@Serializable
data class SpecificHolding(
    val instrumentId: InstrumentId,
    val custodian: Custodian,
    val size: BigDecimal,
    val lastModified: Date
) {
    fun isRival(other: SpecificHolding): Boolean {
        return instrumentId == other.instrumentId && custodian == other.custodian
    }
}