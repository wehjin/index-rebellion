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
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.handleEvents
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.plusHMargin
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputTower
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingAction as Action
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingVision as Vision

@SuppressLint("Registered")
class EditHoldingActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>
    private val visionTower = Standard.BodyTower().mapSight(Vision::toString)

    private val sizeInputTower = TextInputTower<String>()
        .plusHMargin(Standard.uniformMargin).plusVPad(Standard.uniformPad)
        .mapSight { vision: Vision ->
            val topic = "size"
            when (vision) {
                is Vision.Editing -> {
                    val sizeEdit = vision.sizeEdit
                    val text = sizeEdit.novel?.string ?: ""
                    TextInputSight(
                        topic,
                        text,
                        sizeEdit.novel?.selection ?: IntRange(text.length, text.length - 1),
                        sizeEdit.ancient?.validValue?.toString() ?: sizeEdit.seed?.validValue?.toString() ?: "",
                        sizeEdit.label
                    )
                }
                else -> TextInputSight(topic, "", error = "BAD: $vision")
            }
        }
        .handleEvents { Log.d(TAG, "EVENT $it") }

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
    }
}