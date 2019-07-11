package com.rubyhuntersky.vx.tower.towers.edittext

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.edittext.EditTextEvent as Event
import com.rubyhuntersky.vx.tower.towers.edittext.EditTextSight as Sight

class EditTextTower<Topic : Any> : Tower<Sight<Topic>, Event<Topic>> {
    override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight<Topic>, Event<Topic>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}