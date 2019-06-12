package com.rubyhuntersky.vx.dash.dashes

import com.nhaarman.mockitokotlin2.mock
import com.rubyhuntersky.vx.dash.Dash
import com.rubyhuntersky.vx.ViewId
import org.junit.Test

class GapSightDashTest {

    private val view = GapDash.enview(mock(), ViewId())

    @Test
    fun latitudeMatchesGapPixelsCount() {
        view.setSight(GapSight.Pixels(32))
        view.latitudes.test().assertValue(Dash.Latitude(32))
    }
}