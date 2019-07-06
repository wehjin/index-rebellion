package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

class ClickTower<ClickContext : Any> :
    Tower<ClickSight<ClickContext>, ClickEvent<ClickContext>> {

    override fun enview(
        viewHost: Tower.ViewHost,
        id: ViewId
    ): Tower.View<ClickSight<ClickContext>, ClickEvent<ClickContext>> = viewHost.addClickView(id)
}