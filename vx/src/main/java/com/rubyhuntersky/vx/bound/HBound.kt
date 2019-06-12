package com.rubyhuntersky.vx.bound

data class HBound(val start: Int, val end: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun startZero(): HBound = HBound(0, end - start)
}