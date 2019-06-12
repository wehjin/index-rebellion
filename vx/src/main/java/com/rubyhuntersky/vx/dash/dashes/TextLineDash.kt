package com.rubyhuntersky.vx.dash.dashes

import com.rubyhuntersky.vx.dash.Dash
import com.rubyhuntersky.vx.ViewId

data class TextLineSight(
    val text: String,
    val style: TextStyle
)

enum class TextStyle {
    Highlight5,
    Highlight6,
    Subtitle1,
}

class TextLineDash : Dash<TextLineSight, Nothing> {
    override fun enview(viewHost: Dash.ViewHost, id: ViewId): Dash.View<TextLineSight, Nothing> = viewHost.addTextLine(id)
}
