package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioPlan(
    val commodityWeight: Weight,
    val securityWeight: Weight
)