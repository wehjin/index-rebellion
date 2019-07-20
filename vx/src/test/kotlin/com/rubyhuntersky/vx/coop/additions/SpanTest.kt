package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.Span
import org.junit.Assert.assertEquals
import org.junit.Test

class SpanTest {

    @Test
    fun absolute() {
        val span = Span.Absolute(33)
        val real = span.realize(100)
        assertEquals(33, real)
    }

    @Test
    fun relative() {
        val span = Span.Relative(0.2f)
        val real = span.realize(100)
        assertEquals(20, real)
    }
}