package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.toDivisionElementId
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class PortfolioPlan(
    val commodityWeight: Weight,
    val securityWeight: Weight
) : Division {
    init {
        check(component1() >= Weight.ZERO)
        check(component2() >= Weight.ZERO)
        check(component1() + component2() > Weight.ZERO)
    }

    private val aggregateWeight: Weight by lazy { component1() + component2() }

    val commodityPortion: Double by lazy { component1().toPortion(aggregateWeight) }
    val securityPortion: Double by lazy { max(0.0, 1.0 - commodityPortion) }

    override val divisionId
        get() = DivisionId.Portfolio

    override val divisionElements
        get() = listOf(
            DivisionElement(
                id = DivisionId.Cash.toDivisionElementId(),
                weight = commodityWeight
            ),
            DivisionElement(
                id = DivisionId.Securities.toDivisionElementId(),
                weight = securityWeight
            )
        )
}