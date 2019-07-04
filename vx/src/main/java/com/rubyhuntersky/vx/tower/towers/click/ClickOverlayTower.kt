package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

private class ClickOverlayTower<Sight : Any>(
    private val core: Tower<Sight, Nothing>
) : Tower<Sight, ClickEvent> {

    override fun enview(
        viewHost: Tower.ViewHost,
        id: ViewId
    ): Tower.View<Sight, ClickEvent> = viewHost.addClickOverlayView(core, id)
}

fun <Sight : Any> Tower<Sight, Nothing>.plusClicks(): Tower<Sight, ClickEvent> = ClickOverlayTower(this)