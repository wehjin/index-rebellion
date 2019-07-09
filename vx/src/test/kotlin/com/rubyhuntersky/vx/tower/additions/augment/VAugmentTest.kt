package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import org.junit.Assert.assertEquals
import org.junit.Test

class VAugmentTest {

    private val tower1 = EmptyTower<Unit, Nothing>(100)
    private val tower2 = EmptyTower<Unit, Nothing>(50)
    private val emptyTower = EmptyTower<Unit, Nothing>(0)

    @Test
    fun uniform() {
        with(VAugment.Uniform(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun individual() {
        with(VAugment.Individual(tower1, tower2)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun ceiling() {
        with(VAugment.Ceiling(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }

    @Test
    fun floor() {
        with(VAugment.Floor(tower1)) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun none() {
        with(VAugment.None<WrapTextSight, Nothing>()) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }
}