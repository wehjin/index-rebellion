package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory.Vision
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.extend.extendFloors
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight
import com.rubyhuntersky.vx.tower.towers.click.ClickTower
import io.reactivex.disposables.Disposable

class ViewHoldingActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val moveButton = ClickTower<Unit>()
        .fixSight(ButtonSight(Unit, "Move")).mapSight(Vision.Viewing::toUnit)
        .handleEvents { interaction.sendAction(Action.Reclassify) }
        .plusVPad(Standard.uniformPad).plusHMargin(Standard.uniformMargin)

    private val deleteButton = ClickTower<Unit>()
        .fixSight(ButtonSight(Unit, "Delete"))
        .mapSight(Vision.Viewing::toUnit)
        .handleEvents { interaction.sendAction(Action.Delete) }
        .plusVPad(Standard.uniformPad).plusHMargin(Standard.uniformMargin)

    private val buttonBar = moveButton.shareEnd(Span.HALF, deleteButton)

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
                Standard.SubtitleTower().mapSight { viewing: Vision.Viewing -> viewing.plate.memberTag },
                buttonBar
            )
            .plusVPad(Standard.uniformPad)
            .plusHMargin(Standard.uniformMargin)

    private val coopContentView = CoopContentView(pageTower.inCoop())
    private lateinit var eventUpdates: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityInteraction()
        coopContentView.setInActivity(this@ViewHoldingActivity)
        eventUpdates = coopContentView.events.subscribe()
    }

    private fun startActivityInteraction() {
        val activityInteraction = ActivityInteraction(ViewHoldingStory.groupId, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(
        vision: Vision,
        sendAction: (Action) -> Unit,
        edge: Edge
    ) {
        when (vision) {
            is Vision.Viewing -> coopContentView.setSight(vision)
        }
    }

    override fun onBackPressed() = interaction.sendAction(Action.End)

    companion object : ProjectionSource<Vision, Action> {

        override val group: String = ViewHoldingStory.groupId

        override fun startProjection(
            activity: FragmentActivity,
            interaction: Interaction<Vision, Action>,
            key: Long
        ) {
            Intent(activity, ViewHoldingActivity::class.java)
                .also { ActivityInteraction.setInteractionSearchKey(it, key) }
                .let { activity.startActivity(it) }
        }
    }
}
