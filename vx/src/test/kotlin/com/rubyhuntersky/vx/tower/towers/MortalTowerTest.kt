package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.Mortal
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MortalTowerTest {

    private val viewHost = TestTowerViewHost()
    private val viewId = ViewId()

    @Test
    internal fun main() {
        val tower = MortalTower(WrapTextTower())
        val view = tower.enview(viewHost, viewId).apply { setHBound(HBound(0 to 100)) }
        view.setSight(Mortal.Be(WrapTextSight("Hello")))
        assertEquals(1, viewHost.items.size)

        viewHost.items.first().latitudes.onNext(Height(250))
        view.latitudes.test().assertValue(Height(250))

        view.setAnchor(Anchor(30, 0f))
        assertEquals(Anchor(30, 0f), viewHost.items.first().anchor)

        view.setSight(Mortal.NotToBe)
        assertEquals(0, viewHost.items.size)
        view.latitudes.test().assertValue(Height.ZERO)
    }
}