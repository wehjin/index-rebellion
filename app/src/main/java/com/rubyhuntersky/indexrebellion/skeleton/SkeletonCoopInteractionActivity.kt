package com.rubyhuntersky.indexrebellion.skeleton

import android.annotation.SuppressLint
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

@SuppressLint("Registered")
class SkeletonCoopInteractionActivity : AppCompatActivity() {

    private val coopContentView = CoopContentView(Standard.BodyTower().inCoop())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ActivityInteraction(group, this, this::renderVision))
        coopContentView.setInActivity(this)
    }

    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        coopContentView.setSight(vision.toString())
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