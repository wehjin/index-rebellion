package com.rubyhuntersky.vx.common.margin

import com.rubyhuntersky.vx.common.Span
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BiMarginTest {

    private val three = Span.Absolute(3)
    private val five = Span.Absolute(5)
    private val seven = Span.Absolute(7)
    private val eleven = Span.Absolute(11)

    @Test
    fun uniform() {
        with(BiMargin.Uniform(three)) {
            assertEquals(three, startSpan)
            assertEquals(three, endSpan)
            assertEquals(three, ceilingSpan)
            assertEquals(three, floorSpan)
        }
    }

    @Test
    fun uniformHorizontal() {
        with(BiMargin.UniformHorizontal(three)) {
            assertEquals(three, startSpan)
            assertEquals(three, endSpan)
            assertEquals(null, ceilingSpan)
            assertEquals(null, floorSpan)
        }
    }

    @Test
    fun uniformVertical() {
        with(BiMargin.UniformVertical(three)) {
            assertEquals(null, startSpan)
            assertEquals(null, endSpan)
            assertEquals(three, ceilingSpan)
            assertEquals(three, floorSpan)
        }
    }

    @Test
    fun independentAxis() {
        with(BiMargin.AxisIndependent(three, five)) {
            assertEquals(three, startSpan)
            assertEquals(three, endSpan)
            assertEquals(five, ceilingSpan)
            assertEquals(five, floorSpan)
        }
    }

    @Test
    fun independent() {
        with(BiMargin.Independent(three, five, seven, eleven)) {
            assertEquals(three, startSpan)
            assertEquals(five, endSpan)
            assertEquals(seven, ceilingSpan)
            assertEquals(eleven, floorSpan)
        }
    }

    @Test
    fun start() {
        with(BiMargin.Start(three)) {
            assertEquals(three, startSpan)
            assertEquals(null, endSpan)
            assertEquals(null, ceilingSpan)
            assertEquals(null, floorSpan)
        }
    }

    @Test
    fun end() {
        with(BiMargin.End(three)) {
            assertEquals(null, startSpan)
            assertEquals(three, endSpan)
            assertEquals(null, ceilingSpan)
            assertEquals(null, floorSpan)
        }
    }

    @Test
    fun ceiling() {
        with(BiMargin.Ceiling(three)) {
            assertEquals(null, startSpan)
            assertEquals(null, endSpan)
            assertEquals(three, ceilingSpan)
            assertEquals(null, floorSpan)
        }
    }

    @Test
    fun floor() {
        with(BiMargin.Floor(three)) {
            assertEquals(null, startSpan)
            assertEquals(null, endSpan)
            assertEquals(null, ceilingSpan)
            assertEquals(three, floorSpan)
        }
    }

    @Test
    fun none() {
        with(BiMargin.None) {
            assertEquals(null, startSpan)
            assertEquals(null, endSpan)
            assertEquals(null, ceilingSpan)
            assertEquals(null, floorSpan)
        }
    }
}