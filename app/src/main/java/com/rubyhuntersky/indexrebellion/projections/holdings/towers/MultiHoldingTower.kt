package com.rubyhuntersky.indexrebellion.projections.holdings.towers

import com.rubyhuntersky.indexrebellion.projections.holdings.PageSight
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.replicate.replicate

internal object MultiHoldingTower : Tower<PageSight, Nothing>
by HoldingTower
    .replicate()
    .mapSight({ page: PageSight -> page.holdings })
    .neverEvent()