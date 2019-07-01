package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val portfolioPlan: PortfolioPlan,
    val commodityPlan: CommodityPlan,
    val securityPlan: SecurityPlan,
    val equityPlan: EquityPlan
) {
    private val equityPortion: Double by lazy {
        portfolioPlan.securityPortion * securityPlan.equityPortion
    }

    fun portion(plate: Plate): Double = when (plate) {
        Plate.Fiat -> portfolioPlan.commodityPortion * commodityPlan.fiatPortion
        Plate.BlockChain -> portfolioPlan.commodityPortion * commodityPlan.blockChainPortion
        Plate.Debt -> portfolioPlan.securityPortion * securityPlan.debtPortion
        Plate.GlobalEquity -> equityPortion * equityPlan.globalPortion
        Plate.ZonalEquity -> equityPortion * equityPlan.zonalPortion
        Plate.LocalEquity -> equityPortion * equityPlan.localPortion
        Plate.Unknown -> 0.0
    }
}
