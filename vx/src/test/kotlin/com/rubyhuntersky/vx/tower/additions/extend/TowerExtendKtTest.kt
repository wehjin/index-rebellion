package com.rubyhuntersky.vx.tower.additions.extend

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerExtendKtTest {

    private val viewHost = TestTowerViewHost()

    @Test
    fun main() {

        val tower =
            WrapTextTower().extendVertical(VExtend.Uniform(EmptyTower(10)))

        val sight = WrapTextSight("Hello", TextStyle.Body1)
        val view = tower.enview(viewHost, ViewId())
            .apply {
                setSight(sight)
                setHBound(HBound(0, 1000))
                setAnchor(Anchor(0, 0f))
            }
        val item = viewHost.items.first()
            .apply {
                latitudes.onNext(Latitude(100))
            }
        assertEquals(sight, item.sight)
        assertEquals(HBound(0, 1000), item.bound)
        assertEquals(10, item.anchor!!.position)
        view.latitudes.test().assertValue(Latitude(120))
    }
}