package com.rubyhuntersky.vx.tower

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.TitleTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import junit.framework.Assert.assertEquals
import org.junit.Test

class TowerAndTest {
    private val viewId = ViewId()
    private val viewHost = TestTowerViewHost()

    private val tower = TitleTower.mapSight(Pair<String, String>::first) and
            TitleTower.mapSight(Pair<String, String>::second)

    private val view = tower.enview(viewHost, viewId)

    @Test
    fun setSight() {
        view.setSight(Pair("Hello", "World"))
        assertEquals(
            setOf(WrapTextSight("Hello", TextStyle.Highlight5), WrapTextSight("World", TextStyle.Highlight5)),
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
        viewHost.items[0].latitudes.onNext(Latitude(75))
        viewHost.items[1].latitudes.onNext(Latitude(25))
        view.setAnchor(Anchor(0, 0f))
        assertEquals(
            setOf(Anchor(0, 0f), Anchor(75, 0f)),
            viewHost.items.map(TestTowerViewHost.Item::anchor).toSet()
        )
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        viewHost.items[0].latitudes.onNext(Latitude(100))
        viewHost.items[1].latitudes.onNext(Latitude(5))
        test.assertValue(Latitude(105))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}