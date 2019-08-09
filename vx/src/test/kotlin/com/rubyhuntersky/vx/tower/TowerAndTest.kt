package com.rubyhuntersky.vx.tower

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TowerAndTest {
    private val viewId = ViewId()
    private val viewHost = TestTowerViewHost()

    private val tower1 = WrapTextTower().mapSight { it: Pair<String, String> -> WrapTextSight(it.first) }
    private val tower2 = WrapTextTower().mapSight { it: Pair<String, String> -> WrapTextSight(it.second) }
    private val tower = tower1 and tower2

    private val view = tower.enview(viewHost, viewId)

    @Test
    fun setSight() {
        view.setSight(Pair("Hello", "World"))
        assertEquals(
            setOf(WrapTextSight("Hello", TextStyle.Body1), WrapTextSight("World", TextStyle.Body1)),
            viewHost.items.map(TestTowerViewHost.Item::sight).toSet()
        )
    }

    @Test
    fun setLimit() {
        val limit = HBound(0, 20)
        view.setHBound(limit)
        assertEquals(
            setOf(limit),
            viewHost.items.map(TestTowerViewHost.Item::bound).toSet()
        )
    }

    @Test
    fun setAnchor() {
        viewHost.items[0].latitudes.onNext(Height(75))
        viewHost.items[1].latitudes.onNext(Height(25))
        view.setAnchor(Anchor(0, 0f))
        assertEquals(
            setOf(Anchor(0, 0f), Anchor(75, 0f)),
            viewHost.items.map(TestTowerViewHost.Item::anchor).toSet()
        )
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        viewHost.items[0].latitudes.onNext(Height(100))
        viewHost.items[1].latitudes.onNext(Height(5))
        test.assertValue(Height(105))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}