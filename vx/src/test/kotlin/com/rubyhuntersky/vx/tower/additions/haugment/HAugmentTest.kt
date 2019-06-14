package com.rubyhuntersky.vx.tower.additions.haugment

import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import org.junit.Assert.assertEquals
import org.junit.Test

class HAugmentTest {

    private val tower1 = EmptyTower<Unit, Nothing>(100)
    private val tower2 = EmptyTower<Unit, Nothing>(50)
    private val emptyTower = EmptyTower<Unit, Nothing>(0)

    @Test
    fun uniform() {
        with(HAugment.Uniform(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun individualTowers() {
        with(HAugment.Individual(tower1, tower2)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun individualHeights() {
        with(HAugment.Individual<Unit, Nothing>(100, 50)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun ceilingTower() {
        with(HAugment.Ceiling(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }

    @Test
    fun ceilingHeight() {
        with(HAugment.Ceiling<Unit, Nothing>(100)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }

    @Test
    fun floorTower() {
        with(HAugment.Floor(tower1)) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun floorHeight() {
        with(HAugment.Floor<Unit, Nothing>(100)) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(tower1, floorTower)
        }
    }

    @Test
    fun none() {
        with(HAugment.None<TextWrapSight, Nothing>()) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }
}