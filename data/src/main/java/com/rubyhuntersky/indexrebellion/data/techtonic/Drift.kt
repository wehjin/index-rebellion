package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.market.Market
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Plan
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.PlateAdjustment
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plating
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Vault
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.*


@Serializable
data class Drift(
    val vault: Vault,
    val market: Market,
    val plan: Plan,
    val plating: Plating
) {

    val generalHoldings: Set<GeneralHolding> by lazy {
        vault.specificHoldings
            .groupBy { it.instrumentId }
            .map { entry ->
                val instrumentId = entry.key
                val sample = market.findSample(instrumentId)
                val specificHoldings = entry.value
                val size = specificHoldings.map { it.size }.fold(BigDecimal.ZERO, BigDecimal::plus)
                val custodians = specificHoldings.map { it.custodian }.toSet()
                val instrumentName = sample?.instrumentName
                val cashValue = sample?.sharePrice?.times(size.toDouble())
                val sampleModified = sample?.sampleDate ?: Date(0)
                val lastModified = specificHoldings.map { it.lastModified }.fold(sampleModified, Date::later)
                GeneralHolding(instrumentId, size, custodians, instrumentName, cashValue, lastModified)
            }
            .toSet()
    }

    val plateAdjustments: Set<PlateAdjustment> by lazy {
        val (vaultValue, vaultPortions) = vault.toValueAndPortions(plating, market)
        val vaultInstrumentsByPlate = vault.toInstrumentsByPlate(plating)
        Plate.values()
            .map { plate ->
                val plannedPortion = plan.portion(plate)
                val realPortion = vaultPortions[plate] ?: 0.0
                val platingInstruments = plating.findInstrumentIds(plate)
                val vaultInstruments = vaultInstrumentsByPlate[plate] ?: emptySet()
                val instrumentIds = platingInstruments + vaultInstruments
                PlateAdjustment(plate, plannedPortion, realPortion, vaultValue, instrumentIds)
            }
            .toSet()
    }

    fun findHolding(instrumentId: InstrumentId): GeneralHolding? =
        generalHoldings.firstOrNull { it.instrumentId == instrumentId }

    fun replaceSample(sample: InstrumentSample): Drift {
        return copy(market = market.replaceSample(sample))
    }

    fun replaceHolding(holding: SpecificHolding): Drift {
        require(market.contains(holding.instrumentId))
        return copy(vault = vault.replaceHolding(holding))
    }
}


