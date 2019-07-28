package com.rubyhuntersky.indexrebellion.interactions.viewplan

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Plan
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory.Vision
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.precore.StoryPlot

class ViewPlanStory : Interaction<Vision, Action>
by Story(Plot::start, Plot::isEnding, Plot::revise, Plot.name) {

    sealed class Vision {
        object Idle : Vision()
        object Loading : Vision()
        data class Viewing(val plan: Plan) : Vision()
        object Ended : Vision()
    }

    sealed class Action {
        object Start : Action()
        data class Load(val drift: Drift) : Action()
        data class ViewDivision(val divisionId: DivisionId) : Action()
        data class Ignore(val ignore: Any) : Action()
        object End : Action()
    }

    object Plot : StoryPlot<Vision, Action> {

        override val name: String = ViewPlanStory::class.java.simpleName
        override fun start(): Vision = Vision.Idle
        override fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

        override fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
            vision is Vision.Idle && action is Action.Start -> Revision(
                Vision.Loading,
                wish = ReadDrifts.toWish(Action::Load, Action::Ignore)
            )
            (vision is Vision.Loading || vision is Vision.Viewing) && action is Action.Load -> Revision(
                Vision.Viewing(
                    action.drift.plan
                )
            )
            vision is Vision.Viewing && action is Action.ViewDivision -> Revision(
                Vision.Viewing(vision.plan),
                wish = EditDivisionStory.wish(edge, action.divisionId, Action::Ignore)
            )
            action is Action.End -> Revision(Vision.Ended)
            action is Action.Ignore -> Revision<Vision, Action>(vision).also { println(addTag("IGNORED: ${action.ignore} VISION: $vision")) }
            else -> Revision<Vision, Action>(vision).also { System.err.println(addTag("BAD REVISION: $action, $vision")) }
        }
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = Plot.name
    }
}
