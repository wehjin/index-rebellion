package com.rubyhuntersky.indexrebellion.interactions.viewholding

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.GeneralHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story

class ViewHoldingStory : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, TAG) {
    companion object {
        const val TAG = "ViewHoldingStory"
    }
}

sealed class Vision {
    object Idle : Vision()
    data class Reading(val instrumentId: InstrumentId) : Vision()
    data class Viewing(val holding: GeneralHolding, val plate: Plate) : Vision()
}

private fun start(): Vision = Vision.Idle

private fun isEnding(@Suppress("UNUSED_PARAMETER") maybe: Any?): Boolean = false

sealed class Action {
    data class Init(val instrumentId: InstrumentId) : Action()
    data class Load(val drift: Drift) : Action()
}

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> =
    when {
        vision is Vision.Idle && action is Action.Init -> Revision(
            Vision.Reading(action.instrumentId),
            ReadDriftsDjinn.wish("readDrifts", Action::Load)
        )
        vision is Vision.Reading && action is Action.Load -> Revision(
            Vision.Viewing(
                action.drift.findHolding(vision.instrumentId)!!,
                action.drift.plating.findPlate(vision.instrumentId)
            )
        )
        else -> error("${ViewHoldingStory.TAG}: Invalid revision parameters - $vision, $action")
    }
