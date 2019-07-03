package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Action
import com.rubyhuntersky.indexrebellion.interactions.viewholding.VIEW_HOLDING_STORY
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Vision
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.indexrebellion.toLabel
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.tower.additions.augment.extendFloors
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.additions.plusVMargin
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.click.ClickTower

class ViewHoldingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coopContentView.setInActivity(this@ViewHoldingActivity)
        activityInteraction = ActivityInteraction(this, VIEW_HOLDING_STORY, this::renderVision)
        lifecycle.addObserver(activityInteraction)
    }

    private val coopContentView = CoopContentView(pageTower.inCoop())
    private lateinit var activityInteraction: ActivityInteraction<Vision, Action>

    private fun renderVision(vision: Vision) {
        when (vision) {
            is Vision.Viewing -> coopContentView.setSight(vision)
        }
    }

    companion object {

        private val pageTower =
            Standard.TitleTower()
                .mapSight { viewing: Vision.Viewing ->
                    viewing.holding.instrumentName ?: viewing.holding.instrumentId.symbol
                }
                .extendFloors(
                    Standard.SubtitleTower().mapSight { viewing: Vision.Viewing -> "${viewing.holding.size} shares" },
                    Standard.SubtitleTower().mapSight { viewing: Vision.Viewing ->
                        viewing.holding.cashValue?.toDollarStat() ?: "Unknown value"
                    },
                    Standard.SubtitleTower().mapSight { viewing: Vision.Viewing -> viewing.plate.toLabel() },
                    ClickTower().neverEvent<Nothing>().mapSight { ClickSight("Reclassify") }
                )
                .plusHPad(Standard.uniformPad)
                .plusVMargin(Standard.uniformMargin)
    }
}
