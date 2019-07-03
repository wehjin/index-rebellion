package com.rubyhuntersky.vx.tower.towers.wraptext

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

class WrapTextTower : Tower<WrapTextSight, Nothing> {
    override fun enview(viewHost: Tower.ViewHost, id: ViewId) = viewHost.addWrapTextView(id)
}
