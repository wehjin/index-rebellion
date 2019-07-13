package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingAction
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Vision
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanAction
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.vx.android.logChanges
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Action as ViewHoldingAction

class ViewDriftStory : Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision {
        object Idle : Vision()
        object Reading : Vision()
        data class Viewing(val drift: Drift) : Vision()
    }

    sealed class Action {
        object Init : Action()
        data class Load(val drift: Drift) : Action()
        data class ViewHolding(val instrumentId: InstrumentId) : Action()
        data class Ignore(val ignore: Any?) : Action()
        object AddHolding : Action()
        object ViewPlan : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ViewDriftStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(@Suppress("UNUSED_PARAMETER") maybe: Any?): Boolean = false

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
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
    vision is Vision.Viewing && action is Action.AddHolding -> {
        val editHolding = edge.wish(
            "edit-holding",
            interaction = EditHoldingStory().logChanges(EditHoldingStory.groupId),
            startAction = EditHoldingAction.Start(null),
            endVisionToAction = Action::Ignore
        )
        Revision(vision, editHolding)
    }
    vision is Vision.Viewing && action is Action.ViewPlan -> {
        val viewPlan = edge.wish(
            "view-plan",
            interaction = ViewPlanStory(),
            startAction = ViewPlanAction.Start,
            endVisionToAction = Action::Ignore
        )
        Revision(vision, viewPlan)
    }
    action is Action.Ignore -> Revision(vision)
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag(vision, action))
    }
}

private fun addTag(vision: Vision, action: Action): String {
    return "${ViewDriftStory.groupId} BAD REVISION: $action, $vision"
}
