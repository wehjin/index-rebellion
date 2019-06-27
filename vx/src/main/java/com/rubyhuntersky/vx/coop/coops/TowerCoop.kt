package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.tower.Tower

class TowerCoop<Sight : Any, Event : Any>(
    private val tower: Tower<Sight, Event>
) : Coop<Sight, Event> {

    override fun enview(viewHost: Coop.ViewHost, id: ViewId) = viewHost.addTowerView(tower, id)
}