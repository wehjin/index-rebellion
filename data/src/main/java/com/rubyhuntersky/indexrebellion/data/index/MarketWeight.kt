package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.common.BigDecimalSerializer
import com.rubyhuntersky.indexrebellion.data.toStatString
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class MarketWeight(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) {
    constructor(double: Double) : this(BigDecimal.valueOf(double))
    constructor(long: Long) : this(BigDecimal.valueOf(long))

    operator fun plus(other: MarketWeight): MarketWeight =
        MarketWeight(value + other.value)

    operator fun div(other: MarketWeight): Double = value.divide(other.value, 50, BigDecimal.ROUND_HALF_UP).toDouble()

    override fun equals(other: Any?): Boolean = if (other is MarketWeight) {
        value.compareTo(other.value) == 0
    } else {
        false
    }

    override fun hashCode(): Int = value.toDouble().hashCode()

    fun toStatString(): String = value.toDouble().toStatString()
    fun toDouble(): Double = value.toDouble()

    companion object {
        val ZERO = MarketWeight(BigDecimal.ZERO)
        val ONE = MarketWeight(BigDecimal.ONE)
        val TEN = MarketWeight(BigDecimal.TEN)
    }
}