package com.rubyhuntersky.indexrebellion.projections.holdings.towers

import com.rubyhuntersky.indexrebellion.common.MyApplication
import com.rubyhuntersky.indexrebellion.projections.holdings.PageSight
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.margin.plusVMargin
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower

internal object BalanceTower : Tower<PageSight, Nothing>
by WrapTextTower()
    .mapSight({ page: PageSight ->
        WrapTextSight(
            page.balance,
            TextStyle.Highlight5,
            Orbit.Center
        )
    })
    .plusVMargin(Margin.Uniform(MyApplication.standardMarginSpan))
    .plusHPad(
        HPad.Individual(
            MyApplication.standardMarginSize * 3 / 2,
            MyApplication.standardMarginSize / 2
        )
    )
