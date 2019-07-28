package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
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

    override val divisionElements: List<DivisionElement>
        get() = listOf(
            DivisionElement(fiatElementId, fiatWeight),
            DivisionElement(chainElementId, blockChainWeight)
        )

    private val fiatElementId get() = DivisionElementId.Subdivision(DivisionId.Cash)
    private val chainElementId get() = DivisionElementId.Plate(divisionId, Plate.BlockChain)

    override fun replace(divisionElements: List<DivisionElement>): CommodityPlan {
        val weights = divisionElements.associateBy(DivisionElement::id, DivisionElement::weight)
        return copy(
            fiatWeight = weights[fiatElementId] ?: fiatWeight,
            blockChainWeight = weights[chainElementId] ?: blockChainWeight
        )
    }
}