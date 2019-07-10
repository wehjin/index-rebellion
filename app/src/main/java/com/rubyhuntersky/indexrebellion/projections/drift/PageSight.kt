package com.rubyhuntersky.indexrebellion.projections.drift

import com.rubyhuntersky.vx.tower.towers.click.ClickSight

internal data class PageSight(
    val balance: String,
    val holdings: List<HoldingSight>
) {
    fun toAddHoldingClick(): ClickSight<Unit> = ClickSight("+ Holding", Unit)
}