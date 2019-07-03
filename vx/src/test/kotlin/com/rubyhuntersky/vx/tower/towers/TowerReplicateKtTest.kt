package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost.Item
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerReplicateKtTest {

    private val viewHost = TestTowerViewHost()

    @Test
    fun listTower() {
        val sight = listOf(
            WrapTextSight("A"),
            WrapTextSight("B"),
            WrapTextSight("C")
        )
        val tower = WrapTextTower().replicate()
        val view = tower.enview(viewHost, ViewId())
        view.setSight(sight)
        view.setHBound(HBound(0, 100))
        viewHost.items.forEach {
            it.latitudes.onNext(Latitude(20))
        }
        view.setAnchor(Anchor(0))

        // Verify
        assertEquals(
            sight.toSet(),
            viewHost.items.map(Item::sight).toSet()
        )
        assertEquals(
            setOf(HBound(0, 100)),
            viewHost.items.map(Item::bound).toSet()
        )
        view.latitudes.test().assertValue(Latitude(60))
        assertEquals(
            setOf(Anchor(0), Anchor(20), Anchor(40)),
            viewHost.items.map(Item::anchor).toSet()
        )
    }
}