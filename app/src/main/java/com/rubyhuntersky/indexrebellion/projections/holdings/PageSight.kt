package com.rubyhuntersky.indexrebellion.projections.holdings

internal data class PageSight(
    val balance: String,
    val holdings: List<HoldingSight>
)