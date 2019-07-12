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
    private val commodityPortion = portfolioPlan.commodityPortion
    private val fiatPortion = commodityPortion * commodityPlan.fiatPortion
    private val coinPortion = commodityPortion * commodityPlan.blockChainPortion
    private val securityPortion = portfolioPlan.securityPortion
    private val debtPortion = securityPortion * securityPlan.debtPortion
    private val equityPortion = securityPortion * securityPlan.equityPortion
    private val globalPortion = equityPortion * equityPlan.globalPortion
    private val zonalPortion = equityPortion * equityPlan.zonalPortion
    private val localPortion = equityPortion * equityPlan.localPortion

    val divisions
        get() = listOf(portfolioPlan, commodityPlan, securityPlan, equityPlan)

    fun portion(plate: Plate): Double = when (plate) {
        Plate.Fiat -> fiatPortion
        Plate.BlockChain -> coinPortion
        Plate.Debt -> debtPortion
        Plate.GlobalEquity -> globalPortion
        Plate.ZonalEquity -> zonalPortion
        Plate.LocalEquity -> localPortion
        Plate.Unknown -> 0.0
    }
}

