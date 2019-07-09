package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.ClassifyInstrumentStory
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Vision
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.tower.additions.inCoop

class ClassifyInstrumentActivity : AppCompatActivity() {

    private val coopContentView = CoopContentView(Standard.BodyTower().inCoop())
    private lateinit var activityInteraction: ActivityInteraction<Vision, Action>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityInteraction = ActivityInteraction(group, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        coopContentView.setInActivity(this)
    }

    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        coopContentView.setSight(vision.toString())
    }

    override fun onBackPressed() {
        activityInteraction.sendAction(Action.End)
    }

    companion object : ProjectionSource<Vision, Action> {
        override val group: String = ClassifyInstrumentStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            Intent(activity, ClassifyInstrumentActivity::class.java).putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }
    }
}