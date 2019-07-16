package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.market.Market
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plating
import kotlinx.serialization.Serializable

@Serializable
data class Vault(
    val specificHoldings: Set<SpecificHolding>
) {

    fun deleteHoldings(instrumentId: InstrumentId): Vault {
        val new = specificHoldings.filter { it.instrumentId != instrumentId }.toSet()
        return copy(specificHoldings = new)
    }

    fun replaceHolding(holding: SpecificHolding): Vault {
        val new = specificHoldings.toMutableSet().also {
            it.removeAll(holding::isRival)
            it.add(holding)
        }
        return copy(specificHoldings = new)
    }

    fun toValueAndPortions(plating: Plating, market: Market): Pair<CashAmount, Map<Plate, Double>> {
        val plateValues = specificHoldings.fold(
            initial = mutableMapOf<Plate, CashAmount>(),
            operation = { plateValues, holding ->
                val plate = plating.findPlate(holding.instrumentId)
                val sharePrice = market.findSharePrice(holding.instrumentId)!!
                val holdingValue = sharePrice * holding.size
                plateValues.also {
                    it[plate] = (it[plate] ?: CashAmount.ZERO) + holdingValue
                }
            }
        )
        val aggregateValue = plateValues.values.fold(CashAmount.ZERO, CashAmount::plus)
        val portions = plateValues.mapValues {
            if (aggregateValue > CashAmount.ZERO) it.value.toPortion(aggregateValue)
            else 0.0
        }
        return Pair(aggregateValue, portions)
    }

    fun toInstrumentsByPlate(plating: Plating): Map<Plate, Set<InstrumentId>> = specificHoldings.fold(
        initial = mutableMapOf(),
        operation = { instruments, holding ->
            val plate = plating.findPlate(holding.instrumentId)
            instruments.also {
                it[plate] = (it[plate] ?: emptySet()).plus(holding.instrumentId)
            }
        }
    )
}