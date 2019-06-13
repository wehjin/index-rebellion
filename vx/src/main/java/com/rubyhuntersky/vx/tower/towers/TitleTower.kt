package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrap
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import com.rubyhuntersky.vx.transform

object TitleTower : Tower<String, Nothing>
by TextWrapTower().transform({
    TextWrap(it, TextStyle.Highlight5)
})
