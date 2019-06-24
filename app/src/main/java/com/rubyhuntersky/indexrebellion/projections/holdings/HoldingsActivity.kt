package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.interactions.holdings.Action
import com.rubyhuntersky.indexrebellion.interactions.holdings.HoldingsStory
import com.rubyhuntersky.indexrebellion.interactions.holdings.Vision
import com.rubyhuntersky.indexrebellion.projections.holdings.towers.PageTower
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.vx.android.TowerContentView
import com.rubyhuntersky.vx.tower.additions.mapSight

class HoldingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        towerContentView.setInActivity(this@HoldingsActivity)
        activityInteraction = ActivityInteraction(this, HoldingsStory.TAG, this::renderVision)
        lifecycle.addObserver(activityInteraction)
    }

    private val towerContentView = TowerContentView(
        tower = PageTower.mapSight { drift: Drift ->
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

    private lateinit var activityInteraction: ActivityInteraction<Vision, Action>

    private fun renderVision(vision: Vision) {
        when (vision) {
            is Vision.Viewing -> towerContentView.setSight(vision.drift)
        }
    }
}
