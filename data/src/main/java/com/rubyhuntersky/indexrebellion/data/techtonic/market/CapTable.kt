package com.rubyhuntersky.indexrebellion.data.techtonic.market

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId

sealed class CapTable {
    data class Incomplete(val invalidInstruments: Set<InstrumentId>) : CapTable()
    data class Complete(val caps: Map<InstrumentId, MarketCap.Supported>) : CapTable()
}