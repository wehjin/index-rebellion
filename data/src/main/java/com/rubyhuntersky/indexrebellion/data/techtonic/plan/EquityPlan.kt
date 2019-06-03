package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable

@Serializable
data class EquityPlan(
    val globalWeight: Weight,
    val zonalWeight: Weight,
    val localWeight: Weight
)