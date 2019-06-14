package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import org.junit.Assert.assertEquals
import org.junit.Test

class HAugmentTest {

    private val emptyAugment = Augment.empty<TextWrapSight, Nothing>()
    private val span1 = Span.Absolute(3)
    private val tower1 = TextWrapTower()
    private val augment1 = Augment(span1, tower1)
    private val span2 = Span.Relative(0.5f)
    private val tower2 = TextWrapTower()
    private val augment2 = Augment(span2, tower2)

    @Test
    fun uniform() {
        with(HAugment.Uniform(span1, tower1)) {
            assertEquals(augment1, ceilingAugment)
            assertEquals(augment1, floorAugment)
        }
    }

    @Test
    fun individual() {
        with(HAugment.Individual(span1, span2, tower1, tower2)) {
            assertEquals(augment1, ceilingAugment)
            assertEquals(augment2, floorAugment)
        }
    }

    @Test
    fun ceiling() {
        with(HAugment.Ceiling(span1, tower1)) {
            assertEquals(augment1, ceilingAugment)
            assertEquals(emptyAugment, floorAugment)
        }
    }

    @Test
    fun floor() {
        with(HAugment.Floor(span1, tower1)) {
            assertEquals(emptyAugment, ceilingAugment)
            assertEquals(augment1, floorAugment)
        }
    }

    @Test
    fun none() {
        with(HAugment.Empty<TextWrapSight, Nothing>()) {
            assertEquals(emptyAugment, ceilingAugment)
            assertEquals(emptyAugment, floorAugment)
        }
    }
}