package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import kotlinx.serialization.Serializable

@Serializable
data class Vault(
    val specificHoldings: Set<SpecificHolding>
) {
    fun replaceHolding(holding: SpecificHolding): Vault {
        val newHoldings = specificHoldings
            .toMutableSet()
            .also {
                it.removeAll(holding::isRival)
                it.add(holding)
            }
        return copy(specificHoldings = newHoldings)
    }
}