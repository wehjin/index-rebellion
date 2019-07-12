package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.toDivisionElementId
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class EquityPlan(
    val globalWeight: Weight,
    val zonalWeight: Weight,
    val localWeight: Weight
) : Division {
    init {
        check(component1() >= Weight.ZERO)
        check(component2() >= Weight.ZERO)
        check(component3() >= Weight.ZERO)
        check(component1() + component2() + component3() > Weight.ZERO)
    }

    private val aggregateWeight: Weight by lazy { component1() + component2() + component3() }

    val globalPortion: Double by lazy { component1().toPortion(aggregateWeight) }
    val zonalPortion: Double by lazy { component2().toPortion(aggregateWeight) }
    val localPortion: Double by lazy { max(0.0, 1.0 - (globalPortion + zonalPortion)) }

    override val divisionId
        get() = DivisionId.Equities

    override val divisionElements
        get() = listOf(
            DivisionElement(
                id = Plate.GlobalEquity.toDivisionElementId(),
                weight = globalWeight
            ),
            DivisionElement(
                id = Plate.ZonalEquity.toDivisionElementId(),
                weight = zonalWeight
            ),
            DivisionElement(
                id = Plate.LocalEquity.toDivisionElementId(),
                weight = localWeight
            )
        )
}