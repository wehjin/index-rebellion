package com.rubyhuntersky.vx.tower.additions.clicks

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent

internal class PlusClicksTower<Sight : Any, Topic : Any>(
    private val core: Tower<Sight, Nothing>,
    private val sightToTopic: (Sight) -> Topic
) : Tower<Sight, ClickEvent<Topic>> {

    override fun enview(viewHost: Tower.ViewHost, viewId: ViewId):
            Tower.View<Sight, ClickEvent<Topic>> = viewHost.addClickOverlayView(viewId, core, sightToTopic)
}

