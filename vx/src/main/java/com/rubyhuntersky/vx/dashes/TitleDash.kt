package com.rubyhuntersky.vx.dashes

import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.transform

object TitleDash : Dash<String, Nothing>
by TextLineDash().transform({
    TextLineSight(it, TextStyle.Highlight5)
})
