package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
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
                GeneralHolding(
                    instrumentId,
                    size,
                    custodians = specificHoldings.map { it.custodian }.toSet(),
                    instrumentName = sample?.instrumentName,
                    cashValue = sample?.sharePrice?.times(size.toDouble()),
                    lastModified = specificHoldings.map { it.lastModified }.fold(
                        initial = sample?.sampleDate ?: Date(0),
                        operation = Date::later
                    )
                )
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

    val netValue: CashAmount by lazy {
        generalHoldings
            .map(GeneralHolding::cashValue)
            .fold(
                initial = CashAmount.ZERO,
                operation = { total, next -> next?.let { total + it } ?: total }
            )
    }

    fun findHolding(instrumentId: InstrumentId): GeneralHolding? =
        generalHoldings.firstOrNull { it.instrumentId == instrumentId }

    fun findSpecificHoldings(instrumentId: InstrumentId): List<SpecificHolding>? =
        vault.specificHoldings.filter { it.instrumentId == instrumentId }

    fun findSample(instrumentId: InstrumentId): InstrumentSample? = market.findSample(instrumentId)

    fun find(divisionId: DivisionId): Division? = plan.findDivision(divisionId)

    fun replace(sample: InstrumentSample): Drift = copy(market = market.replaceSample(sample))
    fun replace(samples: List<InstrumentSample>): Drift =
        copy(market = market.replaceSamples(samples))

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


