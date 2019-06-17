package com.rubyhuntersky.vx.common

import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.bound.VBound

data class Anchor(
    val position: Int = 0,
    val placement: Float = 0.0f
) {

    fun toVBound(size: Int) = VBound(toBound(size))
    fun toHBound(size: Int) = HBound(toBound(size))

    fun toBound(size: Int): Pair<Int, Int> {
        val a = position - (placement * size).toInt()
        val b = a + size
        return Pair(a, b)
    }

    fun edgeToCore(edgeSize: Int, coreSize: Int, coreOffset: Int): Anchor = copy(
        position = position + coreOffset,
        placement = if (coreSize == 0) {
            0f
        } else {
            placement * edgeSize.toFloat() / coreSize.toFloat()
        }
    )
}
