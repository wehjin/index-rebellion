package com.rubyhuntersky.vx.tower.towers.detailsubdetail

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower

internal object DetailAtopSubdetailTower : Tower<String, Nothing>
by WrapTextTower()
    .mapSight({ text ->
        WrapTextSight(text, TextStyle.Highlight6, Orbit.TailLit)
    })