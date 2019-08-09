package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Duality
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DualityTowerTest {

    @Test
    internal fun main() {
        val viewHost = TestTowerViewHost()
        val tower = DualityTower(WrapTextTower(), WrapTextTower())
        val hidden = Duality.Yin(WrapTextSight("Show Value"))
        val revealed = Duality.Yin(WrapTextSight("3"))

        val view = tower.enview(viewHost, ViewId())
        assertEquals(0, viewHost.items.size)

        view.setSight(hidden)
        assertEquals(
            listOf(WrapTextSight("Show Value")),
            viewHost.items.map(TestTowerViewHost.Item::sight)
        )

        view.setHBound(HBound(0 to 100))
        assertEquals(
            listOf(HBound(0 to 100)),
            viewHost.items.map(TestTowerViewHost.Item::bound)
        )

        viewHost.items.first().latitudes.onNext(Height(250))
        view.latitudes.test().assertValue(Height(250))

        view.setAnchor(Anchor(10, 0f))
        assertEquals(
            listOf(Anchor(10, 0f)),
            viewHost.items.map(TestTowerViewHost.Item::anchor)
        )

        view.setSight(revealed)
        assertEquals(
            listOf(WrapTextSight("3")),
            viewHost.items.map(TestTowerViewHost.Item::sight)
        )
        assertEquals(
            listOf(HBound(0 to 100)),
            viewHost.items.map(TestTowerViewHost.Item::bound)
        )
        viewHost.items.first().latitudes.onNext(Height(10))
        view.latitudes.test().assertValue(Height(10))
    }
}

