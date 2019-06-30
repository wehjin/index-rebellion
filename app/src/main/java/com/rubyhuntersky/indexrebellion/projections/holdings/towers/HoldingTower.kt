package com.rubyhuntersky.indexrebellion.projections.holdings.towers

import com.rubyhuntersky.indexrebellion.projections.holdings.HoldingSight
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleSight
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleTower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.plusVMargin
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.additions.shareEnd
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailSight
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailTower

internal object HoldingTower : Tower<HoldingSight, Nothing>
by TitleSubtitleTower
    .mapSight({ holding: HoldingSight ->
        TitleSubtitleSight(holding.name, holding.custodians.joinToString(", "))
    })
    .shareEnd(
        Span.Relative(0.5f),
        DetailSubdetailTower
            .mapSight { holding: HoldingSight ->
                DetailSubdetailSight(
                    "${holding.count.toEngineeringString()} ${holding.symbol}",
                    "$${holding.value.toEngineeringString()}"
                )
            }
    )
    .plusVMargin(Margin.Uniform(Standard.marginSpan))
    .plusHPad(HPad.Uniform(Standard.marginSize))