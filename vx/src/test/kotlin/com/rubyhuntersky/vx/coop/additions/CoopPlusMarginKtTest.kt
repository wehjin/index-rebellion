package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.margin.BiMargin
import com.rubyhuntersky.vx.coop.coops.FitTextCoop
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoopPlusMarginKtTest {

    @Test
    fun plusMargin() {
        val viewHost = TestCoopViewHost()
        val margin = BiMargin.Independent(
            start = Span.Absolute(1),
            end = Span.Absolute(2),
            ceiling = Span.Absolute(3),
            floor = Span.Relative(0.5f)
        )
        val coop = FitTextCoop(TextStyle.Body1, BiOrbit.StartCenterLit) + margin
        with(coop.enview(viewHost, ViewId())) {
            setSight("PlusMargin")
            setBound(BiBound(0, 100, 0, 200))
        }
        val bound = viewHost.items.first().maybeBound!!
        assertEquals(1, bound.start)
        assertEquals(98, bound.end)
        assertEquals(3, bound.ceiling)
        assertEquals(100, bound.floor)
    }
}