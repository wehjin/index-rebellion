package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.mock
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.ViewId
import org.junit.Test

class GapSightTowerTest {

    private val view = GapTower.enview(mock(), ViewId())

    @Test
    fun latitudeMatchesGapPixelsCount() {
        view.setSight(GapSight.Pixels(32))
        view.latitudes.test().assertValue(Tower.Latitude(32))
    }
}