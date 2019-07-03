package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

class ClickTower : Tower<ClickSight, ClickEvent> {

    override fun enview(
        viewHost: Tower.ViewHost,
        id: ViewId
    ): Tower.View<ClickSight, ClickEvent> = viewHost.addClickView(id)
}