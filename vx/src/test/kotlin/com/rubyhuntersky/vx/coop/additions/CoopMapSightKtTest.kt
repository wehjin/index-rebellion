package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.coops.FitTextCoop
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

class CoopMapSightKtTest {

    @Test
    fun mapSight() {
        val viewHost = TestCoopViewHost()
        val coop = FitTextCoop(TextStyle.Body1, BiOrbit.StartCenterLit).mapSight { int: Int -> int.toString() }
        with(coop.enview(viewHost, ViewId())) {
            setSight(42)
            setBound(BiBound(0, 100, 0, 100))
        }
        assertEquals("42", viewHost.items.first().sight)
    }
}