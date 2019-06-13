package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.margin.BiMargin
import com.rubyhuntersky.vx.coop.coops.SingleTextLineCoop
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

class PlusMarginKtTest {

    @Test
    fun plusMargin() {
        val viewHost = TestCoopViewHost()
        val margin = BiMargin.Independent(
            start = Span.Absolute(1),
            end = Span.Absolute(2),
            ceiling = Span.Absolute(3),
            floor = Span.Relative(0.5f)
        )
        val coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.StartCenterLit) + margin
        with(coop.enview(viewHost, ViewId())) {
            setSight("PlusMargin")
            setBound(BiBound(0, 100, 0, 200))
        }
        val bound = viewHost.items.first().bound!!
        assertEquals(1, bound.start)
        assertEquals(98, bound.end)
        assertEquals(3, bound.ceiling)
        assertEquals(100, bound.floor)
    }
}