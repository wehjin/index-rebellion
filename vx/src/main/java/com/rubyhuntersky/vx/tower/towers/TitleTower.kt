package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import com.rubyhuntersky.vx.transform

object TitleTower : Tower<String, Nothing>
by TextWrapTower().transform({
    TextWrapSight(it, TextStyle.Highlight5)
})
