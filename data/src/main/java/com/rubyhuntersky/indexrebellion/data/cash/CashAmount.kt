package com.rubyhuntersky.indexrebellion.data.cash

import com.rubyhuntersky.indexrebellion.data.common.BigDecimalSerializer
import com.rubyhuntersky.indexrebellion.data.techtonic.toPortion
import com.rubyhuntersky.indexrebellion.data.toStatString
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class CashAmount(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) {
    constructor(long: Long) : this(BigDecimal.valueOf(long))
    constructor(double: Double) : this(BigDecimal.valueOf(double))

    override fun equals(other: Any?): Boolean = if (other is CashAmount) {
        value.compareTo(other.value) == 0
    } else {
        false
    }

    override fun hashCode(): Int {
        val d = value.toDouble()
        val s = d.toString()  // Convert to String because Android doesn't include Double.hashcode until platform 24
        return s.hashCode()
    }

    operator fun compareTo(other: CashAmount): Int = value.compareTo(other.value)
    operator fun plus(increment: CashAmount): CashAmount =
        CashAmount(value + increment.value)

    operator fun plus(increment: CashEquivalent): CashEquivalent = when (increment) {
        is CashEquivalent.Unknown -> CashEquivalent.Unknown()
        is CashEquivalent.Amount -> CashEquivalent.Amount(
            this + increment.cashAmount
        )
    }

    operator fun unaryMinus(): CashAmount =
        CashAmount(-value)

    operator fun minus(rhs: CashAmount): CashAmount =
        CashAmount((value - rhs.value))

    operator fun times(multiplier: Double): CashAmount = CashAmount(value * BigDecimal.valueOf(multiplier))
    operator fun times(multiplier: BigDecimal): CashAmount = CashAmount(value * multiplier)

    operator fun div(divisor: CashAmount): Double = value.divide(divisor.value, 50, BigDecimal.ROUND_HALF_UP).toDouble()

    fun toStatString(suffix: String? = null) = toDouble().toStatString(suffix)
    fun toDouble(): Double = value.toDouble()
    fun toPortion(aggregate: CashAmount): Double = value.toPortion(aggregate.value)
    fun toDollarStat(): String = "$${toStatString()}"

    companion object {
        val ZERO = CashAmount(BigDecimal.ZERO)
        val ONE = CashAmount(BigDecimal.ONE)
        val TEN = CashAmount(BigDecimal.TEN)
    }
}

fun Int.toCashAmount() = CashAmount(this.toLong())
fun Long.toCashAmount() = CashAmount(this)