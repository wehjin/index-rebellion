package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower

object TitleTower : Tower<String, Nothing>
by TextWrapTower().mapSight({
    TextWrapSight(it, TextStyle.Highlight5)
})
