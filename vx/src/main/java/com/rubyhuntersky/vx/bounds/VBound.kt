package com.rubyhuntersky.vx.bounds

data class VBound(val ceiling: Int, val floor: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)
}