package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class EquityPlan(
    val globalWeight: Weight,
    val zonalWeight: Weight,
    val localWeight: Weight
) {
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
}