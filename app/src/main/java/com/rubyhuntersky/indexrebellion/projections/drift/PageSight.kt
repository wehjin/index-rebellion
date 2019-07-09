package com.rubyhuntersky.indexrebellion.projections.drift

internal data class PageSight(
    val balance: String,
    val holdings: List<HoldingSight>
)