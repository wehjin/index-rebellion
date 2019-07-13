package com.rubyhuntersky.indexrebellion.data.techtonic.market

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
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

    val instrumentIds: Set<InstrumentId>
        get() = instrumentSamples.map(InstrumentSample::instrumentId).toSet()

    fun contains(instrumentId: InstrumentId): Boolean = ids.contains(instrumentId)
    fun findSample(instrumentId: InstrumentId): InstrumentSample? = byId[instrumentId]
    fun findSharePrice(instrumentId: InstrumentId): CashAmount? = findSample(instrumentId)?.sharePrice

    fun replaceSample(sample: InstrumentSample): Market =
        copy(instrumentSamples = instrumentSamples.toMutableSet().also { mutable ->
            mutable.removeAll { it.instrumentId == sample.instrumentId }
            mutable.add(sample)
        })

    fun replaceSamples(samples: List<InstrumentSample>): Market =
        samples.fold(this, { partial, next -> partial.replaceSample(next) })
}