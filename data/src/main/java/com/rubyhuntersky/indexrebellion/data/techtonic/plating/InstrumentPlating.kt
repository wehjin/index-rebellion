package com.rubyhuntersky.indexrebellion.data.techtonic.plating

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable

@Serializable
data class InstrumentPlating(
    val instrumentId: InstrumentId,
    val plate: Plate
) {
    fun hasInstrument(other: InstrumentPlating): Boolean = instrumentId == other.instrumentId
}