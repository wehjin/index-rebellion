package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.coop.Coop

data class Share<Sight : Any, Event : Any>(
    val span: Span,
    val coop: Coop<Sight, Event>,
    val type: ShareType
)