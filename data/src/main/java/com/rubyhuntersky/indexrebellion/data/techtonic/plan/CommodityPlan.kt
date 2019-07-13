package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.toDivisionElementId
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class CommodityPlan(
    val fiatWeight: Weight,
    val blockChainWeight: Weight
) : Division {
    init {
        check(component1() >= Weight.ZERO)
        check(component2() >= Weight.ZERO)
        check(component1() + component2() > Weight.ZERO)
    }

    private val aggregateWeight: Weight by lazy { component1() + component2() }

    val fiatPortion: Double by lazy { component1().toPortion(aggregateWeight) }
    val blockChainPortion: Double by lazy { max(0.0, 1.0 - fiatPortion) }

    override val divisionId: DivisionId
        get() = DivisionId.Cash

    override val divisionElements
        get() = listOf(
            DivisionElement(
                id = Plate.Fiat.toDivisionElementId(),
                weight = fiatWeight
            ),
            DivisionElement(
                id = Plate.BlockChain.toDivisionElementId(),
                weight = blockChainWeight
            )
        )
}