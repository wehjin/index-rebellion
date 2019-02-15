package com.rubyhuntersky.vx

import org.junit.Assert.assertEquals
import org.junit.Test

class AnchorTest {
    @Test
    fun toLimit() {
        val position = 0
        val placements = listOf(0f, 0.5f, 1.0f)
        val size = 100
        val bounds = placements.map { Anchor(position, it).toBound(size) }
        val expected = listOf(Pair(0, 100), Pair(-50, 50), Pair(-100, 0))
        assertEquals(expected, bounds)
    }
}