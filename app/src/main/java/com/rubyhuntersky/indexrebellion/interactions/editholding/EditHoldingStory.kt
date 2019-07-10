package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingAction as Action
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingVision as Vision

class EditHoldingStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = EditHoldingStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

@Suppress("IntroduceWhenSubject")
private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    action is Action.End -> {
        Revision(Vision.Ended)
    }
    action is Action.Ignore -> {
        println(addTag("IGNORED: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    vision is Vision.Idle && action is Action.Start -> {
        Revision(Vision.Editing(Unit))
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

private fun addTag(message: String): String = "${EditHoldingStory.groupId} $message"