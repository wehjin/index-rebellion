package com.rubyhuntersky.indexrebellion.data.techtonic.market

import kotlinx.serialization.Serializable

@Serializable
data class Market(
    val instrumentSamples: Set<InstrumentSample>
)