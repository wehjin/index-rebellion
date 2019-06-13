package com.rubyhuntersky.vx.bound

import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.coop.additions.margin.BiMargin

data class BiBound(val hBound: HBound, val vBound: VBound) {

    constructor(start: Int, end: Int, ceiling: Int, floor: Int) : this(HBound(start, end), VBound(ceiling, floor))

    val width: Int get() = hBound.width
    val height: Int get() = vBound.height
    val start: Int get() = hBound.start
    val end: Int get() = hBound.end
    val ceiling: Int get() = vBound.ceiling
    val floor: Int get() = vBound.floor

    fun splitHEnd(span: Span): Pair<BiBound, BiBound> = splitH(hBound::splitEnd, span)
    fun splitHStart(span: Span): Pair<BiBound, BiBound> = splitH(hBound::splitStart, span)
    private fun splitH(splitter: (Span) -> Pair<HBound, HBound>, span: Span): Pair<BiBound, BiBound> {
        val (startBound, endBound) = splitter(span)
        return Pair(copy(hBound = startBound), copy(hBound = endBound))
    }

    fun splitVFloor(span: Span): Pair<BiBound, BiBound> = splitV(vBound::splitFloor, span)
    fun splitVCeiling(span: Span): Pair<BiBound, BiBound> = splitV(vBound::splitCeiling, span)
    private fun splitV(splitter: (Span) -> Pair<VBound, VBound>, span: Span): Pair<BiBound, BiBound> {
        val (ceilingBound, floorBound) = splitter(span)
        return Pair(copy(vBound = ceilingBound), copy(vBound = floorBound))
    }

    fun withMargin(margin: BiMargin): BiBound {
        val width = width
        val startIndent = margin.startSpan?.realize(width) ?: 0
        val endIndent = margin.endSpan?.realize(width) ?: 0
        val height = height
        val ceilingIndent = margin.ceilingSpan?.realize(height) ?: 0
        val floorIndent = margin.floorSpan?.realize(height) ?: 0
        return BiBound(start + startIndent, end - endIndent, ceiling + ceilingIndent, floor - floorIndent)
    }
}