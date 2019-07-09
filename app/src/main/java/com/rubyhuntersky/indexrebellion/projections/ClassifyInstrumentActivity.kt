package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.ClassifyInstrumentStory
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Vision
import com.rubyhuntersky.indexrebellion.toLabel
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.fixSight
import com.rubyhuntersky.vx.tower.additions.handleEvent
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.towers.click.plusClicks

class ClassifyInstrumentActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val plateTower = Standard.BodyTower()
        .mapSight(Plate::toLabel)
        .plusClicks { it }

    private val allPlatesTower = plateTower
        .replicate()
        .fixSight(Plate.values().toList())
        .mapSight(Vision::toUnit)
        .handleEvent {
            val plate = it.value.context
            interaction.sendAction(Action.Write(plate))
        }

    private val instrumentTower = Standard.BodyTower().mapSight(Vision::toString)
    private val bodyTower = instrumentTower.extendFloor(allPlatesTower)
    private val coopContentView = CoopContentView(bodyTower.inCoop())

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
        override val group: String = ClassifyInstrumentStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            Intent(activity, ClassifyInstrumentActivity::class.java).putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }
    }
}