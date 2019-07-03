package com.rubyhuntersky.indexrebellion.projections.holdings.towers

import com.rubyhuntersky.indexrebellion.projections.holdings.PageSight
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.plusVMargin
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower

internal object BalanceTower : Tower<PageSight, Nothing>
by WrapTextTower()
    .mapSight({ page: PageSight ->
        WrapTextSight(
            page.balance,
            TextStyle.Highlight5,
            Orbit.Center
        )
    })
    .plusVMargin(Margin.Uniform(Standard.marginSpan))
    .plusHPad(
        HPad.Individual(
            Standard.marginSize * 3 / 2,
            Standard.marginSize / 2
        )
    )
