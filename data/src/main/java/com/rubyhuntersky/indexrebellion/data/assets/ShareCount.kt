package com.rubyhuntersky.indexrebellion.data.assets

import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import kotlinx.serialization.Serializable

@Serializable
data class ShareCount(val value: Double) {
    constructor(long: Long) : this(long.toDouble())

    operator fun plus(rhs: ShareCount): ShareCount =
        ShareCount(value + rhs.value)

    operator fun minus(rhs: ShareCount): ShareCount =
        ShareCount(value - rhs.value)

    operator fun times(rhs: PriceSample?): CashEquivalent =
        product(rhs, this)

    operator fun compareTo(other: ShareCount): Int = value.compareTo(other.value)

    fun toDouble(): Double = value
    fun toCountString(): String = value.toLong().toString()

    companion object {
        val ZERO = ShareCount(0)
        val ONE = ShareCount(1)
        val TEN = ShareCount(10)
    }
}