package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.PlateAdjustment
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.interactions.holdings.Action
import com.rubyhuntersky.indexrebellion.interactions.holdings.HoldingsStory
import com.rubyhuntersky.indexrebellion.interactions.holdings.Vision
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.projections.holdings.towers.BalanceTower
import com.rubyhuntersky.indexrebellion.projections.holdings.towers.MultiHoldingTower
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.coop.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.augment.extendCeiling
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.replicate.replicate

class DriftActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coopContentView.setInActivity(this@DriftActivity)
        activityInteraction = ActivityInteraction(this, HoldingsStory.TAG, this::renderVision)
        lifecycle.addObserver(activityInteraction)
    }

    private val coopContentView = CoopContentView(pageCoop)

    private lateinit var activityInteraction: ActivityInteraction<Vision, Action>

    private fun renderVision(vision: Vision) {
        when (vision) {
            is Vision.Viewing -> coopContentView.setSight(vision)
        }
    }

    companion object {

        private val holdingsContentTower = MultiHoldingTower.extendCeiling(BalanceTower).mapSight { drift: Drift ->
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
        }

        private val adjustmentTower = Standard.BodyTower().mapSight { adjustment: PlateAdjustment ->
            adjustment.toString()
        }

        private val adjustmentsContentTower =
            adjustmentTower.replicate().neverEvent<Nothing>().mapSight { drift: Drift ->
                drift.plateAdjustments.toList()
            }

        private val pageTower = Standard.SectionTower(
            Pair("Holdings", holdingsContentTower),
            Pair("Adjustments", adjustmentsContentTower)
        )

        private val pageCoop: Coop<Vision.Viewing, Nothing> = pageTower.inCoop().mapSight(Vision.Viewing::drift)
    }
}
