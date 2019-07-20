package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.bound.VBound
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerPlusShareKtTest {

    @Test
    fun towerPlusShare() {
        val main = WrapTextTower()
        val alt = WrapTextTower()

        val combined = main
            .plusShare(HShare.End(Span.Absolute(15), alt))

        val viewHost = TestTowerViewHost()
        val view = combined.enview(viewHost, ViewId())

        // Downstream HBound
        view.setHBound(HBound(0, 100))
        assertEquals(
            setOf(HBound(0, 85), HBound(85, 100)),
            viewHost.items.map { it.bound }.toSet()
        )

        // Upstream Latitude
        viewHost.items[0].latitudes.onNext(Latitude(40))
        viewHost.items[1].latitudes.onNext(Latitude(100))
        view.latitudes.test().assertValue(Latitude(100))

        // Downstream Anchor
        view.setAnchor(Anchor(0, 0f))
        assertEquals(
            setOf(VBound(0, 40), VBound(0, 100)),
            viewHost.items.map { it.anchor!!.toVBound(it.latitudes.value!!.height) }.toSet()
        )
    }
}