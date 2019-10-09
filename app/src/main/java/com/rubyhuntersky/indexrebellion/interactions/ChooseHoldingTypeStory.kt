package com.rubyhuntersky.indexrebellion.interactions

import com.rubyhuntersky.indexrebellion.data.techtonic.DOLLAR_INSTRUMENT
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory.Action
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory.Vision
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.editholding.HoldingEditType
import com.rubyhuntersky.interaction.core.*

class ChooseHoldingTypeStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision {
        data class Choosing(val choices: List<HoldingType>) : Vision()
        data class ChoiceMade(val choice: HoldingType?) : Vision()
    }

    sealed class Action {
        data class Ignore(val ignore: Any) : Action()
        data class Choose(val choice: HoldingType?) : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ChooseHoldingTypeStory::class.java.simpleName
        val DEFAULT_CHOICES = listOf(HoldingType.STOCKS, HoldingType.DOLLARS)
    }
}

private fun start(): Vision = Vision.Choosing(ChooseHoldingTypeStory.DEFAULT_CHOICES)

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.ChoiceMade

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    vision is Vision.Choosing && action is Action.Choose -> {
        action.choice?.let {
            Revision(
                newVision = Vision.ChoiceMade(action.choice),
                wish = edge.wish(
                    name = "ending",
                    interaction = EditHoldingStory(),
                    startAction = EditHoldingStory.Action.Start(it.toHoldingEditType()),
                    endVisionToAction = Action::Ignore
                )
            )
        } ?: Revision(newVision = Vision.ChoiceMade(null))
    }
    action is Action.Ignore -> {
        println(addTag("IGNORED: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

private fun HoldingType.toHoldingEditType() = when (this) {
    HoldingType.STOCKS -> HoldingEditType.Stock
    HoldingType.DOLLARS -> HoldingEditType.FixedInstrument(DOLLAR_INSTRUMENT)
}

private fun addTag(message: String): String = "${ChooseHoldingTypeStory.groupId} $message"