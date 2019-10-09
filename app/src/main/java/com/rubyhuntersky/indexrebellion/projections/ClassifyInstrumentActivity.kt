package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.ClassifyInstrumentStory
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Vision
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.clicks.plusClicks
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.replicate.replicate

class ClassifyInstrumentActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val plateTower = Standard.BodyTower()
        .mapSight(Plate::memberTag)
        .plusClicks { it }

    private val allPlatesTower = plateTower
        .replicate()
        .fixSight(Plate.values().toList().filter { it != Plate.Unknown })
        .mapSight(Vision::toUnit)
        .handleEvents {
            val plate = it.value.topic
            interaction.sendAction(Action.Write(plate))
        }

    private val instrumentTower = Standard.TitleTower()
        .plusHMargin(Standard.uniformMargin).plusVPad(Standard.uniformPad)
        .mapSight { vision: Vision -> (vision as? Vision.Viewing)?.let(Vision.Viewing::instrumentId)?.symbol ?: "" }
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