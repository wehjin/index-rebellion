package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower

sealed class HShare<Sight : Any, Event : Any> {

    abstract val span: Span
    abstract val tower: Tower<Sight, Event>
    abstract fun hostGuestBounds(bound: HBound): Pair<HBound, HBound>

    data class Start<Sight : Any, Event : Any>(
        override val span: Span,
        override val tower: Tower<Sight, Event>
    ) : HShare<Sight, Event>() {
        override fun hostGuestBounds(bound: HBound) = bound.splitStart(span).let { Pair(it.second, it.first) }
    }

    data class End<Sight : Any, Event : Any>(
        override val span: Span,
        override val tower: Tower<Sight, Event>
    ) : HShare<Sight, Event>() {
        override fun hostGuestBounds(bound: HBound) = bound.splitEnd(span)
    }
}