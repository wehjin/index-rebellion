package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import kotlinx.serialization.Serializable

@Serializable
data class Vault(
    val specificHoldings: Set<SpecificHolding>
)