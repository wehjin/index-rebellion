package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Action
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Vision
import com.rubyhuntersky.indexrebellion.toLabel
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.tower.additions.augment.extendFloors
import com.rubyhuntersky.vx.tower.additions.handleEvent
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.additions.plusVMargin
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.click.ClickTower
import io.reactivex.disposables.Disposable

class ViewHoldingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityInteraction = ActivityInteraction(ViewHoldingStory.groupId, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        coopContentView.setInActivity(this@ViewHoldingActivity)
        eventUpdates = coopContentView.events.subscribe()
    }

    private lateinit var activityInteraction: ActivityInteraction<Vision, Action>
    private lateinit var eventUpdates: Disposable

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        when (vision) {
            is Vision.Viewing -> coopContentView.setSight(vision)
        }
    }

    @Suppress("RedundantLambdaArrow")
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
                ClickTower<Unit>()
                    .mapSight { _: Vision.Viewing -> ClickSight("Reclassify", Unit) }
                    .handleEvent { activityInteraction.sendAction(Action.Reclassify) }
            )
            .plusHPad(Standard.uniformPad)
            .plusVMargin(Standard.uniformMargin)

    private val coopContentView = CoopContentView(pageTower.inCoop())

    companion object : ProjectionSource<Vision, Action> {

        override val group: String = ViewHoldingStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            Intent(activity, ViewHoldingActivity::class.java)
                .also { ActivityInteraction.setInteractionSearchKey(it, key) }
                .let { activity.startActivity(it) }
        }
    }
}
