package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower

@Deprecated(message = "Use Standard.TitleTower")
object TitleTower : Tower<String, Nothing>
by WrapTextTower().mapSight({
    WrapTextSight(it, TextStyle.Highlight5)
})
