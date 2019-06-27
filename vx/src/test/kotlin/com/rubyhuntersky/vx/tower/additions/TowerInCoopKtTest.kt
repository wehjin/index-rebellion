package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class TowerInCoopKtTest {

    private val viewHost = TestCoopViewHost()
    private val viewId = ViewId()

    @Test
    fun towerInCoop() {
        val tower = EmptyTower<Unit, Nothing>(100)
        val towerInCoop = tower.inCoop()
        val view = towerInCoop.enview(viewHost, viewId)
        val bound = BiBound(0, 50, 0, 80)
        view.setBound(bound)
        view.setSight(Unit)
        when (val item = viewHost.items.first()) {
            is TestCoopViewHost.Item.FITTEXT -> fail("No a TOWER")
            is TestCoopViewHost.Item.TOWER -> {
                assertEquals(bound, item.maybeBound)
                assertEquals(Unit, item.maybeSight)
            }
        }
    }
}