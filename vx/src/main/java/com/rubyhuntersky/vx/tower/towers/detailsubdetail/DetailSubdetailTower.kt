package com.rubyhuntersky.vx.tower.towers.detailsubdetail

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.augment.HAugment
import com.rubyhuntersky.vx.tower.additions.augment.plusAugment
import com.rubyhuntersky.vx.tower.additions.mapSight

object DetailSubdetailTower : Tower<DetailSubdetailSight, Nothing>
by DetailAtopSubdetailTower
    .mapSight(DetailSubdetailSight::detail)
    .plusAugment(
        HAugment.Floor(
            SubdetailBelowDetailTower.mapSight(DetailSubdetailSight::subdetail)
        )
    )