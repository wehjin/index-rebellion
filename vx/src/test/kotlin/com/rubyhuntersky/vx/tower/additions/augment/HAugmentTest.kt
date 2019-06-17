package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
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
    fun individual() {
        with(HAugment.Individual(tower1, tower2)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun ceiling() {
        with(HAugment.Ceiling(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }

    @Test
    fun floor() {
        with(HAugment.Floor(tower1)) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun none() {
        with(HAugment.None<WrapTextSight, Nothing>()) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }
}