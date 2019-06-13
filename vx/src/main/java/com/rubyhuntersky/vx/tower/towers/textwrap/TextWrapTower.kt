package com.rubyhuntersky.vx.tower.towers.textwrap

import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrap

class TextWrapTower : Tower<TextWrap, Nothing> {
    override fun enview(viewHost: Tower.ViewHost, id: ViewId) = viewHost.addTextWrap(id)
}
