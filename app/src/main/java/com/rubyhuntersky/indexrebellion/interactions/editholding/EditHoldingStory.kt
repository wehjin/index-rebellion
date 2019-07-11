package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.stringedit.Novel
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
    vision is Vision.Idle && action is Action.Start -> Revision(Vision.Editing(action.holding.toHoldingEdit()))
    vision is Vision.Editing && action is Action.SetSize -> {
        val size = action.change.first
        val edit = vision.edit.setSizeNovel(
            if (size.isBlank()) null
            else Novel(size, sizeValidity(size), action.change.second)
        )
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetSymbol -> {
        val symbol = action.change.first
        val edit = vision.edit.setSymbolNovel(
            if (symbol.isBlank()) null
            else Novel(symbol, symbolValidity(symbol), action.change.second)
        )
        Revision(Vision.Editing(edit))
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

fun symbolValidity(symbol: String): Validity<String> {
    val trimmedSymbol = symbol.trim().isBlank()
    return if (trimmedSymbol) Validity.Invalid(symbol.trim(), "No a symbol")
    else Validity.Valid(symbol)
}

private fun sizeValidity(string: String): Validity<BigDecimal> =
    string.toDoubleOrNull()
        ?.let(BigDecimal::valueOf)
        ?.let {
            if (it >= BigDecimal.ZERO) Validity.Valid(it)
            else Validity.Invalid<BigDecimal>(string, "Must be positive")
        }
        ?: Validity.Invalid(string, "Invalid double")

private fun SpecificHolding?.toHoldingEdit(): HoldingEdit = this
    ?.let { HoldingEdit().setSymbolAncient(it.instrumentId.symbol).setSizeAncient(it.size) }
    ?: HoldingEdit()

private fun addTag(message: String): String = "${EditHoldingStory.groupId} $message"