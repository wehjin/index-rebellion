package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.techtonic.DOLLAR_INSTRUMENT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding

sealed class HoldingEditType {

    abstract val defaultSymbol: String?
    abstract val isSymbolEditable: Boolean
    abstract val isPriceEditable: Boolean
    abstract val ancient: SpecificHolding?

    abstract fun toInstrumentId(symbol: String): InstrumentId

    object Stock : HoldingEditType() {
        override val defaultSymbol: String? = null
        override val isSymbolEditable: Boolean = true
        override val isPriceEditable: Boolean = true
        override val ancient: SpecificHolding? get() = null
        override fun toInstrumentId(symbol: String): InstrumentId = InstrumentId(symbol, InstrumentType.StockExchange)
    }

    data class FixedInstrument(val instrumentId: InstrumentId) : HoldingEditType() {
        override val defaultSymbol: String? = instrumentId.symbol
        override val isSymbolEditable: Boolean = false
        override val isPriceEditable: Boolean = false
        override val ancient: SpecificHolding? get() = null
        override fun toInstrumentId(symbol: String): InstrumentId {
            check(symbol == DOLLAR_INSTRUMENT.symbol)
            return DOLLAR_INSTRUMENT
        }
    }
}