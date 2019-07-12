package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.common.BigDecimalSerializer
import com.rubyhuntersky.indexrebellion.data.techtonic.toPortion
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Weight(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) {

    operator fun plus(weight: Weight): Weight = Weight(value.add(weight.value))
    operator fun compareTo(weight: Weight): Int = value.compareTo(weight.value)
    fun toPortion(aggregate: Weight): Double = value.toPortion(aggregate.value)

    companion object {
        val ZERO = Weight(BigDecimal.ZERO)
    }
}

