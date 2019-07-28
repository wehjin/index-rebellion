package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
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

    override val divisionElements: List<DivisionElement>
        get() = listOf(
            DivisionElement(globalElementId, globalWeight),
            DivisionElement(zonalElementId, zonalWeight),
            DivisionElement(localElementId, localWeight)
        )

    private val globalElementId get() = DivisionElementId.Plate(divisionId, Plate.GlobalEquity)
    private val zonalElementId get() = DivisionElementId.Plate(divisionId, Plate.ZonalEquity)
    private val localElementId get() = DivisionElementId.Plate(divisionId, Plate.LocalEquity)

    override fun replace(divisionElements: List<DivisionElement>): EquityPlan {
        val weights = divisionElements.associateBy(DivisionElement::id, DivisionElement::weight)
        return copy(
            globalWeight = weights[globalElementId] ?: globalWeight,
            zonalWeight = weights[zonalElementId] ?: zonalWeight,
            localWeight = weights[localElementId] ?: localWeight
        )
    }
}