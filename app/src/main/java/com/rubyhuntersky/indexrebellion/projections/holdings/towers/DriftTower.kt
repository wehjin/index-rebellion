package com.rubyhuntersky.indexrebellion.projections.holdings.towers

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.projections.holdings.HoldingSight
import com.rubyhuntersky.indexrebellion.projections.holdings.PageSight
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight

internal object DriftTower : Tower<Drift, Nothing>
by PageTower
    .mapSight({ drift: Drift ->
        PageSight(
            balance = "0,00",
            holdings = drift.generalHoldings.map {
                HoldingSight(
                    name = drift.market.findSample(it.instrumentId)!!.instrumentName,
                    custodians = it.custodians.map(Custodian::toString),
                    count = it.size,
                    symbol = it.instrumentId.symbol,
                    value = it.cashValue!!.value
                )
            }
        )
    })