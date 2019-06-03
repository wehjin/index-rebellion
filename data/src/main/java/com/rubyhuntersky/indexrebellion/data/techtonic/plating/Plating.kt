package com.rubyhuntersky.indexrebellion.data.techtonic.plating

import kotlinx.serialization.Serializable

@Serializable
data class Plating(
    val instrumentPlatings: Set<InstrumentPlating>
)