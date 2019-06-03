package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Weight(
    val value: BigDecimal
)