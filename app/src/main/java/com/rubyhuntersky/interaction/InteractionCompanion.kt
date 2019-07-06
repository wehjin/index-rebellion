package com.rubyhuntersky.interaction

import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionSearch

interface InteractionCompanion<Vision : Any, Action : Any> {

    val groupId: String

    val searchByGroup: InteractionSearch
        get() = InteractionSearch.ByName(groupId)

    fun findInEdge(edge: Edge): Interaction<Vision, Action> = edge.findInteraction<Vision, Action>(searchByGroup)
}