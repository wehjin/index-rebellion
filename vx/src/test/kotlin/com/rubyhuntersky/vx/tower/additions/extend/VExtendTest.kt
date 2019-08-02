package com.rubyhuntersky.vx.tower.additions.extend

import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VExtendTest {

    private val tower1 = EmptyTower<Unit, Nothing>(100)
    private val tower2 = EmptyTower<Unit, Nothing>(50)
    private val emptyTower = EmptyTower<Unit, Nothing>(0)

    @Test
    fun uniform() {
        with(VExtend.Uniform(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun individual() {
        with(VExtend.Individual(tower1, tower2)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun ceiling() {
        with(VExtend.Ceiling(tower1)) {
            assertEquals(tower1, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }

    @Test
    fun floor() {
        with(VExtend.Floor(tower1)) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(tower2, floorTower)
        }
    }

    @Test
    fun none() {
        with(VExtend.None<WrapTextSight, Nothing>()) {
            assertEquals(emptyTower, ceilingTower)
            assertEquals(emptyTower, floorTower)
        }
    }
}