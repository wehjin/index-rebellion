package com.rubyhuntersky.vx.bound

import com.rubyhuntersky.vx.coop.additions.Span

data class VBound(val ceiling: Int, val floor: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun splitFloor(span: Span): Pair<VBound, VBound> = split(floor - span.realize(floor - ceiling))
    fun splitCeiling(span: Span): Pair<VBound, VBound> = split(ceiling + span.realize(floor - ceiling))
    private fun split(middle: Int): Pair<VBound, VBound> = Pair(VBound(ceiling, middle), VBound(middle, floor))
}