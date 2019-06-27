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
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.coop.additions.*
import com.rubyhuntersky.vx.coop.coops.FitTextCoop
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight

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

        private val holdingsButtonCoop: Coop<Vision.Viewing, Nothing> =
            FitTextCoop(TextStyle.Highlight5, BiOrbit.Center).mapSight { "Holdings" }

        private val adjustmentButtonCoop: Coop<Vision.Viewing, Nothing> =
            FitTextCoop(TextStyle.Highlight5, BiOrbit.Center).mapSight { "Adjustments" }

        private val controlCoop: Coop<Vision.Viewing, Nothing> = holdingsButtonCoop.plus(
            Share(ShareType.HEnd, Span.Relative(0.5f), adjustmentButtonCoop)
        )

        private val holdingsTower = PageTower.mapSight { drift: Drift ->
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

        private val holdingsCoop = holdingsTower.inCoop().mapSight(Vision.Viewing::drift)

        private val pageCoop: Coop<Vision.Viewing, Nothing> = holdingsCoop
            .plus(
                Share(ShareType.VCeiling, Span.Absolute(48), controlCoop)
            )
    }
}
