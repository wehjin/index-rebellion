package com.rubyhuntersky.vx.coop.additions.margin

import com.rubyhuntersky.vx.coop.additions.Span

sealed class Margin {

    abstract val headSpan: Span?
    abstract val tailSpan: Span?

    data class Uniform(val span: Span) : Margin() {
        override val headSpan: Span? get() = span
        override val tailSpan: Span? get() = span
    }

    data class Independent(
        override val headSpan: Span,
        override val tailSpan: Span
    ) : Margin()

    data class Head(
        override val headSpan: Span
    ) : Margin() {
        override val tailSpan: Span? = null
    }

    data class Tail(
        override val tailSpan: Span
    ) : Margin() {
        override val headSpan: Span? = null
    }

    object None : Margin() {
        override val headSpan: Span? = null
        override val tailSpan: Span? = null
    }
}
