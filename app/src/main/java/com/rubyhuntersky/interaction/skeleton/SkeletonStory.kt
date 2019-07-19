package com.rubyhuntersky.interaction.skeleton

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.skeleton.SkeletonStory.Action
import com.rubyhuntersky.interaction.skeleton.SkeletonStory.Vision

class SkeletonStory : Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision {
        object Idle : Vision()
        object Viewing : Vision()
        object Ended : Vision()
    }

    sealed class Action {
        object Start : Action()
        data class Ignore(val ignore: Any) : Action()
        object End : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = SkeletonStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Start -> {
        Revision(Vision.Viewing)
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

private fun addTag(message: String): String = "${SkeletonStory.groupId} $message"