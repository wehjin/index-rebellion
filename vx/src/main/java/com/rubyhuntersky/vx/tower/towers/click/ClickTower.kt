package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

class ClickTower<Topic : Any> :
    Tower<ButtonSight<Topic>, ClickEvent<Topic>> {

    override fun enview(
        viewHost: Tower.ViewHost,
        viewId: ViewId
    ): Tower.View<ButtonSight<Topic>, ClickEvent<Topic>> = viewHost.addButtonView(viewId)
}