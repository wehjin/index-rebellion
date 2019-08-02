package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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