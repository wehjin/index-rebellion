package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower
import org.junit.Assert.assertEquals
import org.junit.Test

class WrapTextTowerTest {

    private val id = ViewId()
    private val viewHost = TestTowerViewHost()

    @Test
    fun envisionPassesIdAndReturnsHostView() {
        WrapTextTower()
            .enview(viewHost, id)
            .also {
                it.setSight(
                    WrapTextSight("Hello")
                )
                it.setHBound(HBound(0, 100))
                it.setAnchor(Anchor())
            }
        val item = viewHost.items.first()
        assertEquals(id, item.id)
    }
}