package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import org.junit.Assert
import org.junit.Test

class EmptyCoopTest {

    private val viewHost = TestCoopViewHost()

    @Test
    fun testEmptyCoop() {
        with(EmptyCoop.enview(viewHost, ViewId())) {
            setSight(Unit)
            setBound(BiBound(0, 100, 0, 100))
        }
        Assert.assertEquals(
            emptyList<TestCoopViewHost.Item>(),
            viewHost.items
        )
    }
}