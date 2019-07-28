package com.rubyhuntersky.interaction.preandroid

import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.precore.StoryPlot
import com.rubyhuntersky.interaction.preandroid.SkeletonStory.Action
import com.rubyhuntersky.interaction.preandroid.SkeletonStory.Vision

class SkeletonStory : Interaction<Vision, Action> by Story(Plot::start, Plot::isEnding, Plot::revise, Plot.name) {

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

    object Plot : StoryPlot<Vision, Action> {

        override val name: String = "Skeleton"

        override fun start(): Vision = Vision.Idle

        override fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

        override fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
            vision is Vision.Idle && action is Action.Start -> Revision(Vision.Viewing)
            action is Action.End -> Revision(Vision.Ended)
            action is Action.Ignore -> Revision<Vision, Action>(vision).also { println(addTag("IGNORED: ${action.ignore} VISION: $vision")) }
            else -> Revision<Vision, Action>(vision).also { System.err.println(addTag("BAD REVISION: $action, $vision")) }
        }
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = Plot.name
    }
}
