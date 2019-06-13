package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.transform

object TitleTower : Tower<String, Nothing>
by TextLineTower().transform({
    TextLineSight(it, TextStyle.Highlight5)
})
