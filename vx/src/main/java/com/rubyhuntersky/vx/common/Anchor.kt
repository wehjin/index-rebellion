package com.rubyhuntersky.vx.common

import com.rubyhuntersky.vx.common.bound.VBound

data class Anchor(
    val position: Int = 0,
    val placement: Float = 0.0f
) {

    fun toVBound(size: Int) = VBound(toBound(size))
    fun toCeiling(height: Int) = toHead(height)

    fun toBound(size: Int): Pair<Int, Int> {
        val a = toHead(size)
        val b = a + size
        return Pair(a, b)
    }

    private fun toHead(size: Int): Int = position - (placement * size).toInt()

    fun edgeToCore(edgeSize: Int, coreSize: Int, coreOffset: Int): Anchor = copy(
        position = position + coreOffset,
        placement = if (coreSize == 0) {
            0f
        } else {
            placement * edgeSize.toFloat() / coreSize.toFloat()
        }
    )
}
