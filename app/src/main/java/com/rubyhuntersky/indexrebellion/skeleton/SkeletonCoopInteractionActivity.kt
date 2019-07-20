package com.rubyhuntersky.indexrebellion.skeleton

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.skeleton.SkeletonStory
import com.rubyhuntersky.interaction.skeleton.SkeletonStory.Action
import com.rubyhuntersky.interaction.skeleton.SkeletonStory.Vision
import com.rubyhuntersky.vx.android.coop.CoopActivity
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight

class SkeletonCoopInteractionActivity : CoopActivity<Vision, Nothing>() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val visionTower = Standard.BodyTower().mapSight(Vision::toString)
    private val tower = visionTower
    override val activityCoop: Coop<Vision, Nothing> = tower.inCoop()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityInteraction()
    }

    private fun startActivityInteraction() {
        val activityInteraction = ActivityInteraction(group, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        vx.setSight(vision)
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