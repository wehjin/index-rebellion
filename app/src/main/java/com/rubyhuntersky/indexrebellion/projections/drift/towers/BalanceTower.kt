package com.rubyhuntersky.indexrebellion.projections.drift.towers

import com.rubyhuntersky.indexrebellion.projections.drift.PageSight
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.plusHMargin
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
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
    .plusHMargin(Margin.Uniform(Standard.marginSpan))
    .plusVPad(
        VPad.Individual(
            Standard.marginSize * 3 / 2,
            Standard.marginSize / 2
        )
    )
