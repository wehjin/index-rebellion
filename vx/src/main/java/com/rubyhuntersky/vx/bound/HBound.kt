package com.rubyhuntersky.vx.bound

import com.rubyhuntersky.vx.coop.additions.Span

data class HBound(val start: Int, val end: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun startZero(): HBound = HBound(0, end - start)

    fun splitEnd(span: Span): Pair<HBound, HBound> = split(end - span.realize(end - start))
    fun splitStart(span: Span): Pair<HBound, HBound> = split(start + span.realize(end - start))
    private fun split(middle: Int): Pair<HBound, HBound> = Pair(HBound(start, middle), HBound(middle, end))
}