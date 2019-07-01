package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.common.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min

@Serializable
data class Weight(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) {
    operator fun plus(weight: Weight): Weight = Weight(value.add(weight.value))
    operator fun compareTo(weight: Weight): Int = value.compareTo(weight.value)

    fun toPortion(aggregate: Weight): Double {
        check((aggregate >= ZERO))
        check(aggregate >= this)
        return min(1.0, value.divide(aggregate.value, 20, RoundingMode.HALF_UP).toDouble())
    }

    companion object {
        val ZERO = Weight(BigDecimal.ZERO)
    }
}

