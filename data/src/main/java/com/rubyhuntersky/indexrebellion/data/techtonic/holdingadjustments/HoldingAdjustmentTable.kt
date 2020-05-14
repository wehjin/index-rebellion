package com.rubyhuntersky.indexrebellion.data.techtonic.holdingadjustments

import com.rubyhuntersky.indexrebellion.data.techtonic.GeneralHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.market.CapTable
import java.math.BigDecimal

sealed class HoldingAdjustmentTable {
    data class Incomplete(val invalidInstruments: Set<InstrumentId>) : HoldingAdjustmentTable()
    data class Complete(
        val additionalInvestment: BigDecimal,
        val capTable: CapTable.Complete,
        val holdings: Map<InstrumentId, GeneralHolding>
    ) : HoldingAdjustmentTable()
}