package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import kotlinx.serialization.Serializable

@Serializable
data class CustodianAccount(
    val id: String,
    val name: String
)