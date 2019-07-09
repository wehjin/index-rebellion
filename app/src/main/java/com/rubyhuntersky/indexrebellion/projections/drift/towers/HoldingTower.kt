package com.rubyhuntersky.indexrebellion.projections.drift.towers

import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.drift.HoldingSight
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
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
    .plusHMargin(Margin.Uniform(Standard.marginSpan))
    .plusVPad(VPad.Uniform(Standard.marginSize))