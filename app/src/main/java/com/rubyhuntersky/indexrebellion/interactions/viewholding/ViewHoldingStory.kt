package com.rubyhuntersky.indexrebellion.interactions.viewholding

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.GeneralHolding
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.ClassifyInstrumentStory
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory.Vision
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.indexrebellion.spirits.genies.DeleteGeneralHolding
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action as ClassifyInstrumentAction

class ViewHoldingStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision {
        object Idle : Vision()
        data class Reading(val instrumentId: InstrumentId) : Vision()
        data class Viewing(
            val holding: GeneralHolding,
            val plate: Plate,
            val specificHoldings: List<SpecificHolding>
        ) : Vision()

        object Ended : Vision()
    }

    sealed class Action {
        object End : Action()
        data class Ignore(val ignore: Any?) : Action()
        data class Init(val instrumentId: InstrumentId) : Action()
        data class Load(val drift: Drift) : Action()
        object Reclassify : Action()
        object Delete : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ViewHoldingStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

private const val READ_DRIFTS = "readDrifts"
private const val RECLASSIFY = "reclassify"
private const val DELETE_HOLDING = "delete-holding"

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Init -> {
        val readDrifts = ReadDrifts.toWish<ReadDrifts, Action>(
            READ_DRIFTS,
            onResult = Action::Load,
            onError = { error("ReadDrift: $it") }
        )
        Revision(Vision.Reading(action.instrumentId), readDrifts, Wish.none(RECLASSIFY))
    }
    vision is Vision.Reading && action is Action.Load -> {
        val instrumentId = vision.instrumentId
        val holding = action.drift.findHolding(instrumentId)!!
        val plate = action.drift.plating.findPlate(instrumentId)
        val specificHoldings = action.drift.findSpecificHoldings(instrumentId)!!
        Revision(Vision.Viewing(holding, plate, specificHoldings))
    }
    vision is Vision.Viewing && action is Action.Load -> {
        val instrumentId = vision.holding.instrumentId
        val holding = action.drift.findHolding(instrumentId)!!
        val plate = action.drift.plating.findPlate(instrumentId)
        val specificHoldings = action.drift.findSpecificHoldings(instrumentId)!!
        Revision(Vision.Viewing(holding, plate, specificHoldings))
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
    vision is Vision.Viewing && action is Action.Delete -> {
        val deleteHoldings = DeleteGeneralHolding(vision.holding.instrumentId).toWish2(
            name = DELETE_HOLDING,
            onResult = Action::Ignore,
            onError = Action::Ignore
        )
        Revision(Vision.Ended, Wish.none(READ_DRIFTS), deleteHoldings)
    }
    action is Action.End -> Revision(
        Vision.Ended,
        Wish.none(READ_DRIFTS),
        Wish.none(RECLASSIFY),
        Wish.none(DELETE_HOLDING)
    )
    action is Action.Ignore -> Revision(vision)
    else -> error(addTag("BAD ACTION:  $action, $vision"))
}

private fun addTag(message: String): String = "${ViewHoldingStory.groupId} $message"
