package com.rubyhuntersky.vx.dash.dashes

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.dash.Dash

data class TextLineSight(
    val text: String,
    val style: TextStyle
)

class TextLineDash : Dash<TextLineSight, Nothing> {
    override fun enview(viewHost: Dash.ViewHost, id: ViewId): Dash.View<TextLineSight, Nothing> =
        viewHost.addTextLine(id)
}
