package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.techtonic.DOLLAR_INSTRUMENT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding

sealed class HoldingEditType {

    abstract val defaultSymbol: String?
    abstract val isSymbolEditable: Boolean
    abstract val isPriceEditable: Boolean
    abstract val isAccountEditable: Boolean
    abstract val ancient: SpecificHolding?

    abstract fun toInstrumentId(symbol: String): InstrumentId

    object Stock : HoldingEditType() {
        override val defaultSymbol: String? = null
        override val isSymbolEditable: Boolean = true
        override val isPriceEditable: Boolean = true
        override val isAccountEditable: Boolean = true
        override val ancient: SpecificHolding? get() = null
        override fun toInstrumentId(symbol: String): InstrumentId {
            return InstrumentId(symbol, InstrumentType.StockExchange)
        }
    }

    data class FixedInstrument(val instrumentId: InstrumentId) : HoldingEditType() {
        override val defaultSymbol: String? = instrumentId.symbol
        override val isSymbolEditable: Boolean = false
        override val isPriceEditable: Boolean = false
        override val isAccountEditable: Boolean = true
        override val ancient: SpecificHolding? get() = null
        override fun toInstrumentId(symbol: String): InstrumentId {
            check(symbol == DOLLAR_INSTRUMENT.symbol)
            return DOLLAR_INSTRUMENT
        }
    }

    data class ShareCount(val specificHolding: SpecificHolding) : HoldingEditType() {
        override val defaultSymbol: String? = specificHolding.instrumentId.symbol
        override val isSymbolEditable: Boolean = false
        override val isPriceEditable: Boolean = false
        override val isAccountEditable: Boolean = false
        override val ancient: SpecificHolding? = specificHolding
        override fun toInstrumentId(symbol: String): InstrumentId = specificHolding.instrumentId
    }
}