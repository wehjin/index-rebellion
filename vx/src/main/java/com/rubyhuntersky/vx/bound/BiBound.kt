package com.rubyhuntersky.vx.bound

import com.rubyhuntersky.vx.coop.additions.Span

data class BiBound(val hBound: HBound, val vBound: VBound) {

    constructor(start: Int, end: Int, ceiling: Int, floor: Int) : this(HBound(start, end), VBound(ceiling, floor))

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
}