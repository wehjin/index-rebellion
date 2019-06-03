package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val portfolioPlan: PortfolioPlan,
    val commodityPlan: CommodityPlan,
    val securityPlan: SecurityPlan,
    val equityPlan: EquityPlan
)