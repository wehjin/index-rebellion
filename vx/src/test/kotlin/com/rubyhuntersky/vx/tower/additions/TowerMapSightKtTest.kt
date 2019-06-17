package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerMapSightKtTest {

    @Test
    fun mapSight() {
        val viewHost = TestTowerViewHost()
        val tower = WrapTextTower().mapSight { text: String -> WrapTextSight(text, TextStyle.Highlight5) }
        with(tower.enview(viewHost, ViewId())) {
            setSight("Hello")
        }
        assertEquals(WrapTextSight("Hello", TextStyle.Highlight5), viewHost.items.first().sight)
    }
}