package com.rubyhuntersky.vx.common.bound

import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.common.margin.Margin

data class VBound(val ceiling: Int, val floor: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    val height: Int
        get() = floor - ceiling

    fun splitFloor(span: Span): Pair<VBound, VBound> = split(floor - span.realize(height))
    fun splitCeiling(span: Span): Pair<VBound, VBound> = split(ceiling + span.realize(height))
    private fun split(middle: Int): Pair<VBound, VBound> = Pair(VBound(ceiling, middle), VBound(middle, floor))

    fun withMargin(margin: Margin): VBound {
        val height = height
        val ceilingIndent = margin.headSpan?.realize(height) ?: 0
        val floorIndent = margin.tailSpan?.realize(height) ?: 0
        return VBound(ceiling + ceilingIndent, floor - floorIndent)
    }
}