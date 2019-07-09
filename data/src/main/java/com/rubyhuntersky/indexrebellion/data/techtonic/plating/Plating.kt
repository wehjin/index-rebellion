package com.rubyhuntersky.indexrebellion.data.techtonic.plating

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable

@Serializable
data class Plating(
    val instrumentPlatings: Set<InstrumentPlating>
) {
    fun findPlate(instrumentId: InstrumentId): Plate {
        val plating = instrumentPlatings.find { it.instrumentId == instrumentId }
        return plating?.plate ?: Plate.Unknown
    }

    fun findInstrumentIds(plate: Plate): Set<InstrumentId> = instrumentPlatings
        .filter { it.plate == plate }
        .map(InstrumentPlating::instrumentId)
        .toSet()

    fun replace(instrumentPlating: InstrumentPlating) = copy(
        instrumentPlatings = instrumentPlatings.filterNot(instrumentPlating::hasInstrument).plus(instrumentPlating).toSet()
    )
}