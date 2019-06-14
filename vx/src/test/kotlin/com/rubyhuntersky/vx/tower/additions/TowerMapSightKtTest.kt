package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.tower.tools.TestTowerViewHost
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import org.junit.Assert.assertEquals
import org.junit.Test

class TowerMapSightKtTest {

    @Test
    fun mapSight() {
        val viewHost = TestTowerViewHost()
        val tower = TextWrapTower().mapSight { text: String -> TextWrapSight(text, TextStyle.Highlight5) }
        with(tower.enview(viewHost, ViewId())) {
            setSight("Hello")
        }
        assertEquals(TextWrapSight("Hello", TextStyle.Highlight5), viewHost.items.first().sight)
    }
}