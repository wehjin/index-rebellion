package com.rubyhuntersky.vx.tower.towers.click

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapEvent
import com.rubyhuntersky.vx.tower.additions.mapSight

class ClickTower<Topic : Any> :
    Tower<ButtonSight<Topic>, ClickEvent<Topic>> {

    override fun enview(
        viewHost: Tower.ViewHost,
        viewId: ViewId
    ): Tower.View<ButtonSight<Topic>, ClickEvent<Topic>> = viewHost.addButtonView(viewId)
}

fun <Topic : Any> clickTowerOf(label: String): Tower<Topic, Topic> {
    return ClickTower<Topic>()
        .mapSight { edge: Topic -> ButtonSight(edge, label) }
        .mapEvent { core: ClickEvent<Topic> -> core.topic }
}
