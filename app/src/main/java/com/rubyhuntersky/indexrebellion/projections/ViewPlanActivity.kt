package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanAction as Action
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanVision as Vision

class ViewPlanActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val coopContentView = CoopContentView(Standard.BodyTower().inCoop())

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
        coopContentView.setSight(vision.toString())
    }

    override fun onBackPressed() {
        interaction.sendAction(Action.End)
    }

    companion object : ProjectionSource<Vision, Action> {
        override val group: String = ViewPlanStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            Intent(activity, ViewPlanActivity::class.java)
                .putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }
    }
}