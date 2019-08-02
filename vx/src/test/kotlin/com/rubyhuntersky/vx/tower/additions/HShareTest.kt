package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HShareTest {
    @Test
    fun start() {
        with(HShare.Start(Span.Absolute(15), WrapTextTower())) {
            assertEquals(
                Pair(HBound(15, 100), HBound(0, 15)),
                hostGuestBounds(HBound(0, 100))
            )
        }
    }

    @Test
    fun end() {
        with(HShare.End(Span.Relative(0.25f), WrapTextTower())) {
            assertEquals(
                Pair(HBound(0, 75), HBound(75, 100)),
                hostGuestBounds(HBound(0, 100))
            )
        }
    }
}