package com.rubyhuntersky.indexrebellion.interactions.viewholding

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.GeneralHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.ClassifyInstrumentStory
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDrifts
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action as ClassifyInstrumentAction

class ViewHoldingStory : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = "ViewHoldingStory"
    }
}

sealed class Vision {
    object Idle : Vision()
    data class Reading(val instrumentId: InstrumentId) : Vision()
    data class Viewing(val holding: GeneralHolding, val plate: Plate) : Vision()
}

private fun start(): Vision = Vision.Idle

private fun isEnding(@Suppress("UNUSED_PARAMETER") maybe: Any?): Boolean = false

sealed class Action {
    data class Init(val instrumentId: InstrumentId) : Action()
    data class Load(val drift: Drift) : Action()
    object Reclassify : Action()
    data class Ignore(val ignore: Any?) : Action()
}

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    return when {
        vision is Vision.Idle && action is Action.Init -> {
            val readDrifts = ReadDrifts.toWish<ReadDrifts, Action>(
                "readDrifts",
                onResult = Action::Load,
                onAction = { error("ReadDrift: $it") }
            )
            Revision(Vision.Reading(action.instrumentId), readDrifts, Wish.none("reclassify"))
        }
        vision is Vision.Reading && action is Action.Load -> {
            val instrumentId = vision.instrumentId
            val holding = action.drift.findHolding(instrumentId)!!
            val plate = action.drift.plating.findPlate(instrumentId)
            Revision(Vision.Viewing(holding, plate))
        }
        vision is Vision.Viewing && action is Action.Load -> {
            val instrumentId = vision.holding.instrumentId
            val holding = action.drift.findHolding(instrumentId)!!
            val plate = action.drift.plating.findPlate(instrumentId)
            Revision(Vision.Viewing(holding, plate))
        }
        vision is Vision.Viewing && action is Action.Reclassify -> {
            val reclassify = edge.wish(
                "reclassify",
                ClassifyInstrumentStory(),
                startAction = ClassifyInstrumentAction.Start(vision.holding.instrumentId),
                endVisionToAction = Action::Ignore
            )
            Revision(vision, reclassify)
        }
        action is Action.Ignore -> Revision(vision)
        else -> error("${ViewHoldingStory.groupId}: Invalid revision parameters - $vision, $action")
    }
}
