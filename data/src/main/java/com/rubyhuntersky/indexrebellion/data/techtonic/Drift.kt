package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.market.Market
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Plan
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plating
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Vault
import kotlinx.serialization.Serializable


@Serializable
data class Drift(
    val vault: Vault,
    val market: Market,
    val plan: Plan,
    val plating: Plating
)

