package com.rubyhuntersky.indexrebellion.interactions.viewholding

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.GeneralHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.ClassifyInstrumentStory
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action as ClassifyInstrumentAction

class ViewHoldingStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = "ViewHoldingStory"
    }
}

sealed class Vision {
    object Idle : Vision()
    data class Reading(val instrumentId: InstrumentId) : Vision()
    data class Viewing(val holding: GeneralHolding, val plate: Plate) : Vision()
    object Ended : Vision()
}

sealed class Action {
    data class Init(val instrumentId: InstrumentId) : Action()
    data class Load(val drift: Drift) : Action()
    object Reclassify : Action()
    data class Ignore(val ignore: Any?) : Action()
    object End : Action()
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    return when {
        vision is Vision.Idle && action is Action.Init -> {
            val readDrifts = ReadDrifts.toWish<ReadDrifts, Action>(
                READ_DRIFTS,
                onResult = Action::Load,
                onAction = { error("ReadDrift: $it") }
            )
            Revision(Vision.Reading(action.instrumentId), readDrifts, Wish.none(RECLASSIFY))
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
                RECLASSIFY,
                ClassifyInstrumentStory(),
                startAction = ClassifyInstrumentAction.Start(vision.holding.instrumentId),
                endVisionToAction = Action::Ignore
            )
            Revision(vision, reclassify)
        }
        action is Action.End -> Revision(Vision.Ended, Wish.none(READ_DRIFTS), Wish.none(RECLASSIFY))
        action is Action.Ignore -> Revision(vision)
        else -> error("${ViewHoldingStory.groupId}: Invalid revision parameters - $vision, $action")
    }
}

private const val READ_DRIFTS = "readDrifts"
private const val RECLASSIFY = "reclassify"
