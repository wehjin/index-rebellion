package com.rubyhuntersky.interaction.precore

import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Revision

interface StoryPlot<Vision : Any, Action : Any> {

    val name: String

    fun start(): Vision

    fun isEnding(maybe: Any?): Boolean

    fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action>

    fun addTag(message: String): String = "$name $message"

}
