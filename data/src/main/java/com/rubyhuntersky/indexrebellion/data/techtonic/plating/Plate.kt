package com.rubyhuntersky.indexrebellion.data.techtonic.plating

import kotlinx.serialization.Serializable

@Serializable
enum class Plate {
    Fiat,
    BlockChain,
    Debt,
    GlobalEquity,
    ZonalEquity,
    LocalEquity
}