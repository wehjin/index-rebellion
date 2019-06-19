package com.rubyhuntersky.indexrebellion.projections.holdings

import java.math.BigDecimal

internal data class HoldingSight(
    val name: String,
    val custodians: List<String>,
    val count: BigDecimal,
    val symbol: String,
    val value: BigDecimal
)