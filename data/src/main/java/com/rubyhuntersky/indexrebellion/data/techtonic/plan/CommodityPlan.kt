package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable

@Serializable
data class CommodityPlan(
    val fiatWeight: Weight,
    val blockChainWeight: Weight
)