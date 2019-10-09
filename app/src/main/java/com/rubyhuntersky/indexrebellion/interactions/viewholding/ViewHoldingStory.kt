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
import com.rubyhuntersky.indexrebellion.spirits.genies.RemoveSpecificHolding
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.precore.Revisionable
import com.rubyhuntersky.interaction.precore.and
import com.rubyhuntersky.interaction.precore.revision
import com.rubyhuntersky.interaction.precore.spirits.wishFor
import com.rubyhuntersky.indexrebellion.interactions.classifyinstrument.Action as ClassifyInstrumentAction

class ViewHoldingStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision : Revisionable {
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
        data class Remove(val specificHolding: SpecificHolding) : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ViewHoldingStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

private const val RECLASSIFY = "reclassify"
private const val DELETE_HOLDING = "delete-holding"

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Init -> {
        val readDrifts = ReadDrifts.toWish(
            onResult = Action::Load,
            onError = { error("ReadDrift: $it") }
        )
        Vision.Reading(action.instrumentId) and readDrifts and Wish.none(RECLASSIFY)
    }
    vision is Vision.Reading && action is Action.Load -> {
        val instrumentId = vision.instrumentId
        val holding = action.drift.findHolding(instrumentId)!!
        val plate = action.drift.plating.findPlate(instrumentId)
        val specificHoldings = action.drift.findSpecificHoldings(instrumentId)!!
        Vision.Viewing(holding, plate, specificHoldings).revision()
    }
    vision is Vision.Viewing && action is Action.Load -> {
        val instrumentId = vision.holding.instrumentId
        val holding = action.drift.findHolding(instrumentId)
        if (holding == null) {
            Vision.Ended.revision()
        } else {
            val plate = action.drift.plating.findPlate(instrumentId)
            val specificHoldings = action.drift.findSpecificHoldings(instrumentId)!!
            Vision.Viewing(holding, plate, specificHoldings).revision()
        }
    }
    vision is Vision.Viewing && action is Action.Reclassify -> {
        val reclassify = edge.wish(
            RECLASSIFY,
            ClassifyInstrumentStory(),
            startAction = ClassifyInstrumentAction.Start(vision.holding.instrumentId),
            endVisionToAction = Action::Ignore
        )
        vision and reclassify
    }
    vision is Vision.Viewing && action is Action.Delete -> {
        val deleteHoldings = DeleteGeneralHolding(vision.holding.instrumentId).toWish2(
            name = DELETE_HOLDING,
            onResult = Action::Ignore,
            onError = Action::Ignore
        )
        Vision.Ended and ReadDrifts.unwish<Action>() and deleteHoldings
    }
    vision is Vision.Viewing && action is Action.Remove -> {
        vision and wishFor(
            RemoveSpecificHolding(action.specificHolding),
            Action::Load,
            Action::Ignore
        )
    }
    action is Action.End -> {
        Vision.Ended and
                ReadDrifts.unwish<Action>() and
                Wish.none(RECLASSIFY) and
                Wish.none(DELETE_HOLDING) and
                RemoveSpecificHolding.unwish()
    }
    action is Action.Ignore -> vision.revision()
    else -> {
        logError("BAD REVISION: $action, $vision")
        Revision(vision)
    }
}

private fun addTag(message: String): String = "${ViewHoldingStory.groupId} $message"
private fun logError(message: String) = System.err.println(addTag(message))
