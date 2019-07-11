package com.rubyhuntersky.vx.tower.additions.clicks

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent

fun <Sight, Topic> Tower<Sight, Nothing>.plusClicks(sightToTopic: (Sight) -> Topic): Tower<Sight, ClickEvent<Topic>>
        where Sight : Any, Topic : Any {

    return PlusClicksTower(this, sightToTopic)
}
