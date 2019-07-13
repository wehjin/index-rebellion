package com.rubyhuntersky.indexrebellion.skeleton

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.skeleton.Action
import com.rubyhuntersky.interaction.skeleton.SkeletonStory
import com.rubyhuntersky.interaction.skeleton.Vision
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight

class SkeletonCoopInteractionActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val visionTower = Standard.BodyTower()
        .mapSight(Vision::toString)

    private val tower = visionTower

    private val coopContentView = CoopContentView(tower.inCoop())

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

    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        coopContentView.setSight(vision)
    }

    override fun onBackPressed() {
        interaction.sendAction(Action.End)
    }

    companion object : ProjectionSource<Vision, Action> {
        override val group: String = SkeletonStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            Intent(activity, SkeletonCoopInteractionActivity::class.java)
                .putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }
    }
}