package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class SecurityPlan(
    val debtWeight: Weight,
    val equityWeight: Weight
) : Division {
    init {
        check(component1() >= Weight.ZERO)
        check(component2() >= Weight.ZERO)
        check(component1() + component2() > Weight.ZERO)
    }

    private val aggregateWeight: Weight by lazy { component1() + component2() }

    val debtPortion: Double by lazy { component1().toPortion(aggregateWeight) }
    val equityPortion: Double by lazy { max(0.0, 1.0 - debtPortion) }

    override val divisionId
        get() = DivisionId.Securities

    override val divisionElements: List<DivisionElement>
        get() = listOf(
            DivisionElement(debtElementId, debtWeight),
            DivisionElement(equitiesElementId, equityWeight)
        )

    private val debtElementId get() = DivisionElementId.Plate(divisionId, Plate.Debt)
    private val equitiesElementId get() = DivisionElementId.Subdivision(DivisionId.Equities)

    override fun replace(divisionElements: List<DivisionElement>): SecurityPlan {
        val weights = divisionElements.associateBy(DivisionElement::id, DivisionElement::weight)
        return copy(
            debtWeight = weights[debtElementId] ?: debtWeight,
            equityWeight = weights[equitiesElementId] ?: equityWeight
        )
    }
}