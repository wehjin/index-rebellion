package com.rubyhuntersky.vx.common.margin

import com.rubyhuntersky.vx.common.Span

sealed class BiMargin {

    abstract val hMargin: Margin
    abstract val vMargin: Margin

    val startSpan: Span? get() = hMargin.headSpan
    val endSpan: Span? get() = hMargin.tailSpan
    val ceilingSpan: Span? get() = vMargin.headSpan
    val floorSpan: Span? get() = vMargin.tailSpan

    data class Uniform(val span: Span) : BiMargin() {
        override val hMargin: Margin =
            Margin.Uniform(span)
        override val vMargin: Margin =
            Margin.Uniform(span)
    }

    data class UniformHorizontal(val span: Span) : BiMargin() {
        override val hMargin: Margin =
            Margin.Uniform(span)
        override val vMargin: Margin = Margin.None
    }

    data class UniformVertical(val span: Span) : BiMargin() {
        override val hMargin: Margin = Margin.None
        override val vMargin: Margin =
            Margin.Uniform(span)
    }

    data class AxisIndependent(val hSpan: Span, val vSpan: Span) : BiMargin() {
        override val hMargin: Margin =
            Margin.Uniform(hSpan)
        override val vMargin: Margin =
            Margin.Uniform(vSpan)
    }

    data class Independent(val start: Span, val end: Span, val ceiling: Span, val floor: Span) : BiMargin() {
        override val hMargin: Margin =
            Margin.Independent(start, end)
        override val vMargin: Margin =
            Margin.Independent(ceiling, floor)
    }

    data class Start(val span: Span) : BiMargin() {
        override val hMargin: Margin =
            Margin.Head(span)
        override val vMargin: Margin = Margin.None
    }

    data class End(val span: Span) : BiMargin() {
        override val hMargin: Margin =
            Margin.Tail(span)
        override val vMargin: Margin = Margin.None
    }

    data class Ceiling(val span: Span) : BiMargin() {
        override val hMargin: Margin = Margin.None
        override val vMargin: Margin =
            Margin.Head(span)
    }

    data class Floor(val span: Span) : BiMargin() {
        override val hMargin: Margin = Margin.None
        override val vMargin: Margin =
            Margin.Tail(span)
    }

    object None : BiMargin() {
        override val hMargin: Margin = Margin.None
        override val vMargin: Margin = Margin.None
    }
}