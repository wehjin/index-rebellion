package com.rubyhuntersky.interaction.skeleton

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story

class SkeletonStory :
    Interaction<Vision, Action> by Story(
        ::start, ::isEnding, ::revise,
        groupId
    ) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = SkeletonStory::class.java.simpleName
    }
}

sealed class Vision {
    object Idle : Vision()
    object Ended : Vision()
}

sealed class Action {
    data class Ignore(val ignore: Any) : Action()
    object End : Action()
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

@Suppress("IntroduceWhenSubject")
private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    action is Action.End -> {
        Revision(Vision.Ended)
    }
    action is Action.Ignore -> {
        println(addTag("IGNORE: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    else -> error(addTag("ACTION: $action VISION: $vision"))
}

private fun addTag(message: String): String = "${SkeletonStory.groupId} $message"