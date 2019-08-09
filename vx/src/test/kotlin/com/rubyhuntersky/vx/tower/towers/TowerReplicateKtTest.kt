package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost.Item
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TowerReplicateKtTest {

    private val viewHost = TestTowerViewHost()

    private val sight = listOf(
        WrapTextSight("A"),
        WrapTextSight("B"),
        WrapTextSight("C")
    )

    val tower = WrapTextTower().replicate()
    val view = tower.enview(viewHost, ViewId())
        .also { view ->
            view.setSight(sight)
            view.setHBound(HBound(0, 100))
            viewHost.items.forEach {
                it.latitudes.onNext(Height(20))
            }
            view.setAnchor(Anchor(0))
        }

    @Test
    fun listTower() {
        // First sight
        assertEquals(
            sight.toSet(),
            viewHost.items.map(Item::sight).toSet()
        )
        assertEquals(
            setOf(HBound(0, 100)),
            viewHost.items.map(Item::bound).toSet()
        )
        view.latitudes.test().assertValue(Height(60))
        assertEquals(
            setOf(Anchor(0), Anchor(20), Anchor(40)),
            viewHost.items.map(Item::anchor).toSet()
        )

        // Shorten the sight
        val shortSight = listOf(
            WrapTextSight("A"),
            WrapTextSight("C")
        )
        view.setSight(shortSight)
        assertEquals(
            shortSight.toSet(),
            viewHost.items.map(Item::sight).toSet()
        )
        view.latitudes.test().assertValue(Height(40))
        assertEquals(
            setOf(Anchor(0), Anchor(20)),
            viewHost.items.map(Item::anchor).toSet()
        )
    }
}