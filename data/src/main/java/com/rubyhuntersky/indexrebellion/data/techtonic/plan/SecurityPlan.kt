package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.toDivisionElementId
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

    override val divisionElements
        get() = listOf(
            DivisionElement(
                id = Plate.Debt.toDivisionElementId(),
                weight = debtWeight
            ),
            DivisionElement(
                id = DivisionId.Equities.toDivisionElementId(),
                weight = equityWeight
            )
        )
}