package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDrifts
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.vx.android.logChanges
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Action as ViewHoldingAction

class ViewDriftStory : Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {
    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = "ViewDriftStory"
    }
}

sealed class Vision {
    object Idle : Vision()
    object Reading : Vision()
    data class Viewing(val drift: Drift) : Vision()
}

private fun start(): Vision = Vision.Idle
private fun isEnding(@Suppress("UNUSED_PARAMETER") maybe: Any?): Boolean = false

sealed class Action {
    object Init : Action()
    data class Load(val drift: Drift) : Action()
    data class ViewHolding(val instrumentId: InstrumentId) : Action()
    data class Ignore(val ignore: Any?) : Action()
}

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    return when {
        vision is Vision.Idle && action is Action.Init -> {
            val readDrifts = ReadDrifts.toWish<ReadDrifts, Action>(
                "read",
                onResult = Action::Load,
                onAction = { error("ReadDrift: $it") }
            )
            Revision(Vision.Reading, readDrifts)
        }
        (vision is Vision.Reading || vision is Vision.Viewing) && action is Action.Load -> {
            Revision(Vision.Viewing(action.drift))
        }
        vision is Vision.Viewing && action is Action.ViewHolding -> {
            val viewHolding = edge.wish(
                "view-holding",
                interaction = ViewHoldingStory().logChanges(ViewHoldingStory.groupId),
                startAction = ViewHoldingAction.Init(action.instrumentId),
                endVisionToAction = Action::Ignore
            )
            Revision(vision, viewHolding)
        }
        action is Action.Ignore -> Revision(vision)
        else -> error("${ViewDriftStory.groupId}: Invalid revision parameters - $vision, $action")
    }
}
