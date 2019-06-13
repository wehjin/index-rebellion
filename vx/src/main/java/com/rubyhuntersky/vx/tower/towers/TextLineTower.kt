package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.tower.Tower

data class TextLineSight(
    val text: String,
    val style: TextStyle
)

class TextLineTower : Tower<TextLineSight, Nothing> {
    override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<TextLineSight, Nothing> =
        viewHost.addTextLine(id)
}
