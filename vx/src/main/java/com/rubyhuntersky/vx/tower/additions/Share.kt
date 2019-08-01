package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.Tower

data class Share<Sight : Any, Event : Any>(
    val span: Span,
    val tower: Tower<Sight, Event>
)
