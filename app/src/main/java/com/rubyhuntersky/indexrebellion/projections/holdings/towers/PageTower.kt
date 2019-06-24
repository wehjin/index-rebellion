package com.rubyhuntersky.indexrebellion.projections.holdings.towers

import com.rubyhuntersky.indexrebellion.projections.holdings.PageSight
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor

internal object PageTower : Tower<PageSight, Nothing>
by BalanceTower
    .extendFloor(MultiHoldingTower)