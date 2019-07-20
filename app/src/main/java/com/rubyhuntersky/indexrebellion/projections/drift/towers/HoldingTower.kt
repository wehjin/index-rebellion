package com.rubyhuntersky.indexrebellion.projections.drift.towers

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.drift.HoldingSight
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailSight
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailTower

internal object HoldingTower : Tower<HoldingSight, Nothing>
by TitleSubtitleTower
    .mapSight({ holding: HoldingSight ->
        val title = holding.name
        val subtitle = holding.plate.memberTag
        TitleSubtitleSight(title, subtitle)
    })
    .shareEnd(
        Span.THIRD,
        DetailSubdetailTower
            .mapSight { holding: HoldingSight ->
                val detail = "${holding.count.toEngineeringString()} ${holding.symbol}"
                val subdetail = CashAmount(holding.value).toDollarStat()
                DetailSubdetailSight(detail, subdetail)
            }
    )
    .plusHMargin(Margin.Uniform(Standard.marginSpan))
    .plusVPad(VPad.Uniform(Standard.marginSize))