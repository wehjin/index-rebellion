package com.rubyhuntersky.vx.tower.towers.detailsubdetail

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.extend.VExtend
import com.rubyhuntersky.vx.tower.additions.extend.extendVertical
import com.rubyhuntersky.vx.tower.additions.mapSight

object DetailSubdetailTower : Tower<DetailSubdetailSight, Nothing>
by DetailAtopSubdetailTower
    .mapSight(DetailSubdetailSight::detail)
    .extendVertical(
        VExtend.Floor(
            SubdetailBelowDetailTower.mapSight(DetailSubdetailSight::subdetail)
        )
    )
