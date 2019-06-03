package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable

@Serializable
data class SecurityPlan(
    val debtWeight: Weight,
    val equityWeight: Weight
)