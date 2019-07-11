package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

class ClickTower<Topic : Any> :
    Tower<ClickSight<Topic>, ClickEvent<Topic>> {

    override fun enview(
        viewHost: Tower.ViewHost,
        id: ViewId
    ): Tower.View<ClickSight<Topic>, ClickEvent<Topic>> = viewHost.addClickView(id)
}