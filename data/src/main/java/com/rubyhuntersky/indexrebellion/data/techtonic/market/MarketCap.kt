package com.rubyhuntersky.indexrebellion.data.techtonic.market

import java.math.BigDecimal

sealed class MarketCap {

    operator fun plus(other: MarketCap): MarketCap {
        return when (this) {
            is Supported -> when (other) {
                is Supported -> Supported(value + other.value)
                NotSupported -> NotSupported
            }
            NotSupported -> NotSupported
        }
    }

    data class Supported(val value: BigDecimal) : MarketCap()
    object NotSupported : MarketCap()
}