package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.stringedit.Novel
import com.rubyhuntersky.interaction.stringedit.Seed
import com.rubyhuntersky.interaction.stringedit.StringEdit
import com.rubyhuntersky.interaction.stringedit.Validity
import java.math.BigDecimal
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingAction as Action
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingVision as Vision

class EditHoldingStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = EditHoldingStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

@Suppress("IntroduceWhenSubject")
private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    action is Action.End -> {
        Revision(Vision.Ended)
    }
    action is Action.Ignore -> {
        println(addTag("IGNORED: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    vision is Vision.Idle && action is Action.Start -> {
        val sizeEdit = StringEdit<BigDecimal>("Shares", seed = Seed(BigDecimal.ZERO, true))
        Revision(Vision.Editing(sizeEdit))
    }
    vision is Vision.Editing && action is Action.SetSize -> {
        val sizeString = action.change.first
        val sizeEdit = vision.sizeEdit.setNovel(Novel(sizeString, sizeValidity(sizeString), action.change.second))
        Revision(Vision.Editing(sizeEdit))
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

private fun sizeValidity(string: String): Validity<BigDecimal> = string.toDoubleOrNull()
    ?.let(BigDecimal::valueOf)
    ?.let {
        if (it >= BigDecimal.ZERO) Validity.Valid(it)
        else Validity.Invalid<BigDecimal>(string, "Must be positive")
    }
    ?: Validity.Invalid(string, "Invalid double")

private fun addTag(message: String): String = "${EditHoldingStory.groupId} $message"