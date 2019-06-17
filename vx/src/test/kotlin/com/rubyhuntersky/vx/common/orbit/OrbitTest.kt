package com.rubyhuntersky.vx.common.orbit

import org.junit.Assert.assertEquals
import org.junit.Test

class OrbitTest {
    @Test
    fun headLit() {
        with(Orbit.HeadLit) {
            assertEquals(0.0f, pole)
            assertEquals(0.0f, swing)
        }
    }

    @Test
    fun headDim() {
        with(Orbit.HeadDim) {
            assertEquals(0.0f, pole)
            assertEquals(1.0f, swing)
        }
    }

    @Test
    fun tailLit() {
        with(Orbit.TailLit) {
            assertEquals(1.0f, pole)
            assertEquals(1.0f, swing)
        }
    }

    @Test
    fun tailDim() {
        with(Orbit.TailDim) {
            assertEquals(1.0f, pole)
            assertEquals(0.0f, swing)
        }
    }

    @Test
    fun center() {
        with(Orbit.Center) {
            assertEquals(0.5f, pole)
            assertEquals(0.5f, swing)
        }
    }

    @Test
    fun custom() {
        with(Orbit.Custom(0.4f, 0.7f)) {
            assertEquals(0.4f, pole)
            assertEquals(0.7f, swing)
        }
    }
}