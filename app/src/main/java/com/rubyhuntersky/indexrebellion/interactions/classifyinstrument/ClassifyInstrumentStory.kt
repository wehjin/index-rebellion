package com.rubyhuntersky.indexrebellion.interactions.classifyinstrument

import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.InstrumentPlating
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDrifts
import com.rubyhuntersky.indexrebellion.spirits.writeinstrumentplate.WriteInstrumentPlating
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.wish.Wish

class ClassifyInstrumentStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = ClassifyInstrumentStory::class.java.simpleName
    }
}

sealed class Vision {
    object Idle : Vision()
    data class Reading(val instrumentId: InstrumentId) : Vision()
    data class Viewing(val instrumentId: InstrumentId, val plate: Plate) : Vision()
    object Ended : Vision()
}

sealed class Action {
    data class Ignore(val ignore: Any) : Action()
    data class Start(val instrumentId: InstrumentId) : Action()
    data class Load(val instrumentId: InstrumentId, val plate: Plate) : Action()
    data class Write(val plate: Plate) : Action()
    object End : Action()
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Action.End

private const val READ_DRIFTS = "read-drifts"

@Suppress("IntroduceWhenSubject")
private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    vision is Vision.Idle && action is Action.Start -> {
        val instrumentId = action.instrumentId
        val readDrifts = ReadDrifts.toWish<ReadDrifts, Action>(
            READ_DRIFTS,
            onResult = { Action.Load(instrumentId, it.plating.findPlate(instrumentId)) },
            onAction = Action::Ignore
        )
        Revision(Vision.Reading(instrumentId), readDrifts)
    }
    (vision is Vision.Reading || vision is Vision.Viewing) && action is Action.Load -> {
        Revision(Vision.Viewing(action.instrumentId, action.plate))
    }
    vision is Vision.Viewing && action is Action.Write -> {
        val writePlate = WriteInstrumentPlating(InstrumentPlating(vision.instrumentId, action.plate))
            .toWish<WriteInstrumentPlating, Action>(
                "write-plating",
                onResult = Action::Ignore,
                onAction = { error("WritePlate: $it") }
            )
        Revision(vision, writePlate)
    }
    action is Action.End -> {
        Revision(Vision.Ended, Wish.none(READ_DRIFTS))
    }
    action is Action.Ignore -> {
        println(addTag("IGNORED: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    else -> error(addTag("ACTION: $action VISION: $vision"))
}

private fun addTag(message: String): String = "${ClassifyInstrumentStory.groupId} $message"