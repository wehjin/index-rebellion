package com.rubyhuntersky.indexrebellion.projections

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.android.toTextInputSight
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.handleEvents
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingAction as Action
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingVision as Vision

@SuppressLint("Registered")
class EditHoldingActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>
    private val visionTower = Standard.BodyTower().mapSight(Vision::toString)


    private val sizeInputTower = Standard.TextInputTower<String>()
        .mapSight { vision: Vision ->
            (vision as? Vision.Editing)?.sizeEdit
                ?.toTextInputSight(SIZE_TOPIC)
                ?: TextInputSight(SIZE_TOPIC, "", error = "BAD: $vision")
        }
        .handleEvents { event ->
            Log.d(TAG, "EVENT $event")
            (event as TextInputEvent.Changed)
                .mapTopic(SIZE_TOPIC) { Action.SetSize(Pair(event.text, event.selection)) }
                ?.let(interaction::sendAction)
        }

    private val pageTower = visionTower.extendFloor(sizeInputTower)
    private val coopContentView = CoopContentView(pageTower.inCoop())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityInteraction()
        coopContentView.setInActivity(this)
    }

    private fun startActivityInteraction() {
        val activityInteraction = ActivityInteraction(group, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        coopContentView.setSight(vision)
    }

    override fun onBackPressed() {
        interaction.sendAction(Action.End)
    }

    companion object : ProjectionSource<Vision, Action> {
        override val group: String = EditHoldingStory.groupId

        override fun startProjection(
            activity: FragmentActivity,
            interaction: Interaction<Vision, Action>,
            key: Long
        ) {
            Intent(activity, EditHoldingActivity::class.java)
                .putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }

        private val TAG = EditHoldingActivity::class.java.simpleName
        private const val SIZE_TOPIC = "size"
    }
}