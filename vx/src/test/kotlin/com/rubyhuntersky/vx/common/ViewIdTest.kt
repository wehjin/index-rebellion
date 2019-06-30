package com.rubyhuntersky.vx.common

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewIdTest {

    private val a0 = ViewId().extend(0)
    private val a1 = a0.extend(0).extend(0)
    private val b0 = ViewId().extend(1)
    private val b1 = b0.extend(1).extend(1)

    @Test
    fun isDescendantOfWithLongerId() {
        assertFalse(a0.isEqualOrExtends(b1))
        assertFalse(b0.isEqualOrExtends(b1))
    }

    @Test
    fun isDescendantOfWithShorterId() {
        assertFalse(b1.isEqualOrExtends(a0))
        assertTrue(b1.isEqualOrExtends(b0))
    }

    @Test
    fun isDescendantOfWithSameLengthId() {
        assertTrue(b1.isEqualOrExtends(b1))
        assertFalse(b1.isEqualOrExtends(a1))
    }
}