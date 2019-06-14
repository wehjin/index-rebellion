package com.rubyhuntersky.vx.tower.towers.textwrap

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

class TextWrapTower : Tower<TextWrapSight, Nothing> {
    override fun enview(viewHost: Tower.ViewHost, id: ViewId) = viewHost.addTextWrap(id)
}
