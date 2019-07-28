package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.coop.Coop

data class Share<in Sight: Any, Event : Any>(
    val type: ShareType,
    val span: Span,
    val coop: Coop<Sight, Event>
)