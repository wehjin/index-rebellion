package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import kotlinx.serialization.Serializable

@Serializable
data class CustodianAccount(
    val id: String,
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CustodianAccount) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id.hashCode()
}