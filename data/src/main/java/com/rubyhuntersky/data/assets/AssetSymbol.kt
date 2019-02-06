package com.rubyhuntersky.data.assets

import kotlinx.serialization.Serializable

@Serializable
data class AssetSymbol(val string: String) {
    override fun toString(): String = string.trim().toUpperCase()
}