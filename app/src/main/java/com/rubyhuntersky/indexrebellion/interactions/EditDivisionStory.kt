package com.rubyhuntersky.indexrebellion.interactions

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionId
import com.rubyhuntersky.indexrebellion.edit.DivisionEdit
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory.Action
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory.Vision
import com.rubyhuntersky.indexrebellion.projections.ElementChange
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.indexrebellion.spirits.genies.WriteDivision
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.edit.Ancient
import com.rubyhuntersky.interaction.precore.StoryPlot


class EditDivisionStory : Interaction<Vision, Action> by Story(Plot::start, Plot::isEnding, Plot::revise, Plot.name) {

    sealed class Vision {
        object Idle : Vision()
        data class Loading(val divisionId: DivisionId) : Vision()
        data class Editing(val divisionEdit: DivisionEdit) : Vision()
        object Ended : Vision()
    }

    sealed class Action {
        data class Start(val divisionId: DivisionId) : Action()
        data class Load(val drift: Drift) : Action()
        data class ChangeElement(val elementChange: ElementChange) : Action()
        object Save : Action()
        data class Ignore(val ignore: Any) : Action()
        object End : Action()
    }

    object Plot : StoryPlot<Vision, Action> {

        override val name: String = EditDivisionStory::class.java.simpleName

        override fun start(): Vision = Vision.Idle

        override fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

        override fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
            vision is Vision.Idle && action is Action.Start -> Revision(
                Vision.Loading(action.divisionId),
                wish = ReadDrifts.toWish(Action::Load, Action::Ignore)
            )
            vision is Vision.Loading && action is Action.Load -> Revision(
                Vision.Editing(DivisionEdit(Ancient(action.drift.find(vision.divisionId)!!))),
                wish = Wish.none(ReadDrifts.defaultWishName)
            )
            vision is Vision.Editing && action is Action.ChangeElement -> {
                Revision(Vision.Editing(vision.divisionEdit.updateElement(action.elementChange)))
            }
            vision is Vision.Editing && action is Action.Save ->
                vision.divisionEdit.writableValue?.let {
                    Revision(
                        Vision.Ended,
                        wish = WriteDivision(it).toWish(Action::Ignore, Action::Ignore)
                    )
                } ?: Revision(vision)
            action is Action.End -> Revision(Vision.Ended)
            action is Action.Ignore -> Revision<Vision, Action>(vision).also { println(addTag("IGNORED: ${action.ignore} VISION: $vision")) }
            else -> Revision<Vision, Action>(vision).also { System.err.println(addTag("BAD REVISION: $action, $vision")) }
        }
    }


    companion object : InteractionCompanion<Vision, Action> {

        override val groupId: String = Plot.name

        private val wishName: String = "${Plot.name}Wish"

        fun <EndActionType : Any> wish(
            edge: Edge,
            divisionId: DivisionId,
            toEndReport: (Vision) -> EndActionType,
            name: String = wishName
        ): Wish<Interaction<Vision, Action>, EndActionType> {
            return edge.wish(name, EditDivisionStory(), Action.Start(divisionId), toEndReport)
        }

        val wishAway = Wish.none<Action>(wishName)
    }
}

