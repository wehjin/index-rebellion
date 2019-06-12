package com.rubyhuntersky.vx

import com.rubyhuntersky.vx.bounds.HBound
import com.rubyhuntersky.vx.bounds.VBound

data class Anchor(val position: Int, val placement: Float) {

    fun toVBound(size: Int) = VBound(toBound(size))
    fun toHBound(size: Int) = HBound(toBound(size))

    fun toBound(size: Int): Pair<Int, Int> {
        val a = position - (placement * size).toInt()
        val b = a + size
        return Pair(a, b)
    }
}
