package com.rubyhuntersky.vx.tower.additions.pad

import org.junit.Assert.assertEquals
import org.junit.Test

class HPadTest {

    private val height1 = 100
    private val height2 = 50
    private val noHeight = 0

    @Test
    fun uniform() {
        with(HPad.Uniform(height1)) {
            assertEquals(height1, ceilingHeight)
            assertEquals(height1, floorHeight)
        }
    }

    @Test
    fun individual() {
        with(HPad.Individual(height1, height2)) {
            assertEquals(height1, ceilingHeight)
            assertEquals(height2, floorHeight)
        }
    }

    @Test
    fun ceiling() {
        with(HPad.Ceiling(height1)) {
            assertEquals(height1, ceilingHeight)
            assertEquals(noHeight, floorHeight)
        }
    }

    @Test
    fun floor() {
        with(HPad.Floor(height1)) {
            assertEquals(noHeight, ceilingHeight)
            assertEquals(height1, floorHeight)
        }
    }

    @Test
    fun none() {
        with(HPad.None) {
            assertEquals(noHeight, ceilingHeight)
            assertEquals(noHeight, floorHeight)
        }
    }
}