package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.coop.coops.SingleTextLineCoop
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

class MapSightKtTest {

    @Test
    fun testMapping() {
        val viewHost = TestCoopViewHost()
        val coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.StartCenterLit) / { int: Int -> int.toString() }
        with(coop.enview(viewHost, ViewId())) {
            setSight(42)
            setBound(BiBound(0, 100, 0, 100))
        }
        assertEquals(
            listOf("42"),
            viewHost.items.map { (it as TestCoopViewHost.Item.SingleTextLine).sight!! }
        )
    }
}