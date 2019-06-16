package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerPlusHAugmentKtTest {

    @Test
    fun plusHAugment() {
        val sight = TextWrapSight("Hello", TextStyle.Body1)
        val tower = TextWrapTower().plus(HAugment.Uniform(EmptyTower(10)))
        val viewHost = TestTowerViewHost()
        val view = tower.enview(viewHost, ViewId())
        with(view) {
            setSight(sight)
            setHBound(HBound(0, 1000))
            setAnchor(Anchor(0, 0f))
        }

        val item = viewHost.items.first()
        item.latitudes.onNext(Latitude(100))
        assertEquals(sight, item.sight)
        assertEquals(HBound(0, 1000), item.bound)
        view.latitudes.test().assertValue(Latitude(120))
        assertEquals(10, item.anchor!!.position)
    }
}