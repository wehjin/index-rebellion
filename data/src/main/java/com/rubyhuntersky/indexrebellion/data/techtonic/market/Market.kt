package com.rubyhuntersky.indexrebellion.data.techtonic.market

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable

@Serializable
data class Market(
    val instrumentSamples: Set<InstrumentSample>
) {
    private val ids: Set<InstrumentId>
            by lazy {
                instrumentSamples.map { it.instrumentId }.toSet()
            }

    private val byId: Map<InstrumentId, InstrumentSample>
            by lazy {
                instrumentSamples.associateBy { it.instrumentId }
            }

    fun contains(instrumentId: InstrumentId): Boolean {
        return ids.contains(instrumentId)
    }

    fun findSample(instrumentId: InstrumentId): InstrumentSample? {
        return byId[instrumentId]
    }

    fun replaceSample(sample: InstrumentSample): Market {
        return copy(
            instrumentSamples = instrumentSamples.toMutableSet()
                .also { samples ->
                    samples.removeAll { it.instrumentId == sample.instrumentId }
                    samples.add(sample)
                }
        )
    }
}