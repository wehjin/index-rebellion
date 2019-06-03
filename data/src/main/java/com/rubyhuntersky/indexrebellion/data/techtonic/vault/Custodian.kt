package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import kotlinx.serialization.Serializable

@Serializable
enum class Custodian {
    None,
    Robinhood,
    Etrade
}