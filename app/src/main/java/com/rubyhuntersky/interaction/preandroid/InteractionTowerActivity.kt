package com.rubyhuntersky.interaction.preandroid

import android.os.Bundle
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.tower.TowerActivity
import com.rubyhuntersky.vx.tower.Tower

abstract class InteractionTowerActivity<Vision : Any, Action : Any>(
    private val interactionName: String,
    private val backAction: Action
) :
    TowerActivity<Vision, Nothing>() {

    abstract override val activityTower: Tower<Vision, Nothing>

    internal lateinit var interaction: Interaction<Vision, Action>

    override fun onBackPressed() = interaction.sendAction(backAction)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityInteraction = ActivityInteraction(interactionName, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) = vx.setSight(vision)
}
