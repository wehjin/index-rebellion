package com.rubyhuntersky.vx.dash.dashes

import com.rubyhuntersky.vx.dash.Dash
import com.rubyhuntersky.vx.transform

object TitleDash : Dash<String, Nothing>
by TextLineDash().transform({
    TextLineSight(it, TextStyle.Highlight5)
})
