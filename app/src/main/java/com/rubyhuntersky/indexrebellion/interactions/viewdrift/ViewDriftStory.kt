package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.InteractionCompanion
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Action as ViewHoldingAction

class ViewDriftStory : Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, VIEW_DRIFT_STORY) {
    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = VIEW_DRIFT_STORY
    }
}

const val VIEW_DRIFT_STORY = "ViewDriftStory"

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
    data class Ignore<out T>(val ignore: T) : Action()
}

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    println("$VIEW_DRIFT_STORY ACTION: $action VISION: $vision")
    return when {
        vision is Vision.Idle && action is Action.Init -> Revision(
            Vision.Reading,
            ReadDriftsDjinn.wish("read", Action::Load)
        )
        vision is Vision.Reading && action is Action.Load -> Revision(
            Vision.Viewing(action.drift)
        )
        vision is Vision.Viewing && action is Action.ViewHolding -> {
            val viewHolding = edge.wish(
                name = "view-holding",
                interaction = ViewHoldingStory(),
                startAction = ViewHoldingAction.Init(action.instrumentId),
                endVisionToAction = Action::Ignore
            )
            Revision(vision, viewHolding)
        }
        action is Action.Ignore<*> -> Revision(vision)
        else -> error("$VIEW_DRIFT_STORY: Invalid revision parameters - $vision, $action")
    }
}
