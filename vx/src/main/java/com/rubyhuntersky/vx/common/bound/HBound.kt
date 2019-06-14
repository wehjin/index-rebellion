package com.rubyhuntersky.vx.common.bound

import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.common.margin.Margin

data class HBound(val start: Int, val end: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    val width: Int
        get() = end - start

    fun startZero(): HBound = HBound(0, width)

    fun splitEnd(span: Span): Pair<HBound, HBound> = split(end - span.realize(width))
    fun splitStart(span: Span): Pair<HBound, HBound> = split(start + span.realize(width))
    private fun split(middle: Int): Pair<HBound, HBound> = Pair(HBound(start, middle), HBound(middle, end))

    fun withMargin(margin: Margin): HBound {
        val width = width
        val startIndent = margin.headSpan?.realize(width) ?: 0
        val endIndent = margin.tailSpan?.realize(width) ?: 0
        return HBound(start + startIndent, end - endIndent)
    }
}