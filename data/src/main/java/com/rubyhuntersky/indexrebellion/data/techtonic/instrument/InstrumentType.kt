package com.rubyhuntersky.indexrebellion.data.techtonic.instrument

import kotlinx.serialization.Serializable

@Serializable
enum class InstrumentType {
    StockExchange,
    Fiat,
    BlockChain
}

