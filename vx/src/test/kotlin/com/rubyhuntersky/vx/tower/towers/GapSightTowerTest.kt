package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.mock
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.Latitude
import org.junit.jupiter.api.Test

class GapSightTowerTest {

    private val view = GapTower.enview(mock(), ViewId())

    @Test
    fun latitudeMatchesGapPixelsCount() {
        view.setSight(GapSight.Pixels(32))
        view.latitudes.test().assertValue(Latitude(32))
    }
}