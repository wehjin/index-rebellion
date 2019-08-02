package com.rubyhuntersky.indexrebellion.projections.drift.towers

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.drift.HoldingSight
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleSight
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleTower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailSight
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailTower

private val nameGroup =
    TitleSubtitleTower.mapSight { holding: HoldingSight ->
        val title = holding.name
        val subtitle = holding.plate.memberTag
        TitleSubtitleSight(title, subtitle)
    }

private val countValue =
    DetailSubdetailTower.mapSight { holding: HoldingSight ->
        val detail = "${holding.count.toEngineeringString()} ${holding.symbol}"
        val subdetail = CashAmount(holding.value).toDollarStat()
        DetailSubdetailSight(detail, subdetail)
    }

internal object HoldingTower : Tower<HoldingSight, Nothing> by nameGroup shl Span.HALF[countValue] pad Standard.spacing
