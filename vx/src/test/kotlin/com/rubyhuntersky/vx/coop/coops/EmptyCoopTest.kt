package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EmptyCoopTest {

    private val viewHost = TestCoopViewHost()

    @Test
    fun testEmptyCoop() {
        with(EmptyCoop.enview(viewHost, ViewId())) {
            setSight(Unit)
            setBound(BiBound(0, 100, 0, 100))
        }
        assertEquals(0, viewHost.items.size)
    }
}