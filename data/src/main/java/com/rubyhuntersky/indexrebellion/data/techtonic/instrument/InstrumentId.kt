package com.rubyhuntersky.indexrebellion.data.techtonic.instrument

import kotlinx.serialization.Serializable

@Serializable
data class InstrumentId(val symbol: String, val type: InstrumentType) {
    init {
        require(symbol == symbol.capitalize().trim())
    }
}