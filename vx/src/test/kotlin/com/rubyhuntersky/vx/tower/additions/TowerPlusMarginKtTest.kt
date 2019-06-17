package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.tower.additions.margin.plus
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerPlusMarginKtTest {

    @Test
    fun plusMargin() {
        val sight = TextWrapSight("0,00", TextStyle.Highlight6)
        val viewHost = TestTowerViewHost()
        val tower = TextWrapTower()
            .plus(
                Margin.Independent(
                    headSpan = Span.Absolute(1),
                    tailSpan = Span.Relative(0.5f)
                )
            )
        val view = tower.enview(viewHost, ViewId())
        with(view) {
            setSight(sight)
            setHBound(HBound(0, 100))
            setAnchor(Anchor(0, 0.0f))
        }

        val item = viewHost.items.first() as TestTowerViewHost.Item.TestTextWrap
        assertEquals(sight, item.sight)
        view.latitudes.map { it.height }.test().let {
            item.latitudes.onNext(Latitude(100))
            it.assertValue(100)
        }
        assertEquals(1, item.bound!!.start)
        assertEquals(50, item.bound!!.end)
    }
}