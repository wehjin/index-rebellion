package com.rubyhuntersky.indexrebellion.data.techtonic.holdingadjustments

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.GeneralHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.market.CapTable
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import java.math.BigDecimal

fun Drift.getHoldingAdjustmentTable(
    plate: Plate,
    additionalInvestment: BigDecimal
): HoldingAdjustmentTable {
    val instruments = getInstruments(plate)
    return when (val capTable = market.getCapTable(instruments)) {
        is CapTable.Incomplete -> HoldingAdjustmentTable.Incomplete(capTable.invalidInstruments)
        is CapTable.Complete -> {
            val holdings =
                instruments.mapNotNull { findHolding(it) }.associateBy(GeneralHolding::instrumentId)
            HoldingAdjustmentTable.Complete(additionalInvestment, capTable, holdings)
        }
    }
}
