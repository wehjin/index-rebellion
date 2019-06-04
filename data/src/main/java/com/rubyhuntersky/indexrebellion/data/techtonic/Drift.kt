package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.market.Market
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Plan
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plating
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Vault
import kotlinx.serialization.Serializable


@Serializable
data class Drift(
    val vault: Vault,
    val market: Market,
    val plan: Plan,
    val plating: Plating
) {

    fun replaceSample(sample: InstrumentSample): Drift {
        return copy(market = market.replaceSample(sample))
    }

    fun replaceHolding(holding: SpecificHolding): Drift {
        require(market.contains(holding.instrumentId))
        return copy(vault = vault.replaceHolding(holding))
    }
}


