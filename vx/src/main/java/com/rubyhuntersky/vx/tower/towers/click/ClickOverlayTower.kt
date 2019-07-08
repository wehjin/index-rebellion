package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

private class ClickOverlayTower<Sight : Any, ClickContext : Any>(
    private val core: Tower<Sight, Nothing>,
    private val sightToClickContext: (Sight) -> ClickContext
) : Tower<Sight, ClickEvent<ClickContext>> {

    override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, ClickEvent<ClickContext>> {
        return viewHost.addClickOverlayView(core, sightToClickContext, id)
    }
}

fun <Sight : Any, ClickContext : Any> Tower<Sight, Nothing>.plusClicks(sightToClickContext: (Sight) -> ClickContext):
        Tower<Sight, ClickEvent<ClickContext>> {

    return ClickOverlayTower(this, sightToClickContext)
}
