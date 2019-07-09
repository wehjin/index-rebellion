package com.rubyhuntersky.indexrebellion.data.techtonic.plating

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import java.math.BigDecimal

@Suppress("EqualsOrHashCode")
data class PlateAdjustment(
    val plate: Plate,
    val plannedPortion: Double,
    val realPortion: Double,
    val vaultValue: CashAmount,
    val instruments: Set<InstrumentId>
) {

    private val toPlannedPortion: Double by lazy { plannedPortion - realPortion }

    val toPlannedValue: Double by lazy { (BigDecimal(toPlannedPortion) * vaultValue.value).toDouble() }

    val realValue: CashAmount by lazy { vaultValue * realPortion }

    override fun hashCode(): Int {
        var result = plate.hashCode()
        result = 31 * result + plannedPortion.toString().hashCode()
        result = 31 * result + realPortion.toString().hashCode()
        result = 31 * result + vaultValue.hashCode()
        result = 31 * result + instruments.hashCode()
        return result
    }
}