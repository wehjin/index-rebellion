package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.market.Market
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Plan
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.InstrumentPlating
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

    fun findSample(instrumentId: InstrumentId): InstrumentSample? = market.findSample(instrumentId)

    fun find(divisionId: DivisionId): Division? = plan.findDivision(divisionId)

    fun replace(sample: InstrumentSample): Drift = copy(market = market.replaceSample(sample))
    fun replace(samples: List<InstrumentSample>): Drift = copy(market = market.replaceSamples(samples))
    fun replace(division: Division): Drift = copy(plan = plan.replace(division))

    fun replace(holding: SpecificHolding): Drift {
        require(market.contains(holding.instrumentId))
        return copy(vault = vault.replaceHolding(holding))
    }

    fun replace(instrumentPlating: InstrumentPlating): Drift {
        return copy(plating = plating.replace(instrumentPlating))
    }

    fun deleteHoldings(instrumentId: InstrumentId): Drift {
        val new = vault.deleteHoldings(instrumentId)
        return copy(vault = new)
    }
}


