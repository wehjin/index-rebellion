package com.rubyhuntersky.vx.tower.towers.textwrap

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.orbit.Orbit

data class WrapTextSight(
    val text: String,
    val style: TextStyle = TextStyle.Body1,
    val orbit: Orbit = Orbit.HeadLit
)