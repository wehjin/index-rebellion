package com.rubyhuntersky.indexrebellion.interactions.viewplan

import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDrifts
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanAction as Action
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanVision as Vision

class ViewPlanStory : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ViewPlanStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

@Suppress("IntroduceWhenSubject")
private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Start -> {
        val load = ReadDrifts.toWish<ReadDrifts, Action>(
            name = "load",
            onResult = Action::Load,
            onAction = Action::Ignore
        )
        Revision(Vision.Loading, load)
    }
    vision is Vision.Loading && action is Action.Load -> {
        Revision(Vision.Viewing(action.drift.plan))
    }
    action is Action.End -> {
        Revision(Vision.Ended)
    }
    action is Action.Ignore -> {
        println(addTag("IGNORED: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

private fun addTag(message: String): String = "${ViewPlanStory.groupId} $message"