package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
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
    vision is Vision.Idle && action is Action.Start -> {
        val edit = action.holding.toHoldingEdit()
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetSize -> {
        val size = action.change.first
        val novel = size.toNovel(action.change.second, ::sizeValidity)
        val edit = vision.edit.setSizeNovel(novel)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetSymbol -> {
        val symbol = action.change.first
        val novel = symbol.toNovel(action.change.second, ::symbolValidity)
        val edit = vision.edit.setSymbolNovel(novel)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetPrice -> {
        val price = action.change.first
        val novel = price.toNovel(action.change.second, ::priceValidity)
        val edit = vision.edit.setPriceNovel(novel)
        Revision(Vision.Editing(edit))
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

private fun <T : Any> String.toNovel(selection: IntRange, toValidity: (String) -> Validity<T>): Novel<T>? =
    if (isBlank()) null
    else Novel(this, toValidity(this), selection)

private fun priceValidity(price: String): Validity<CashAmount> {
    val cashPrice = price.toBigDecimalOrNull()?.let(::CashAmount)
    return when {
        cashPrice != null && cashPrice > CashAmount.ZERO -> Validity.Valid(cashPrice)
        else -> Validity.Invalid(price, "Must be positive")
    }
}

private fun symbolValidity(symbol: String): Validity<String> {
    val trimmedSymbol = symbol.trim()
    return when {
        trimmedSymbol.isBlank() -> Validity.Invalid(symbol, "No a symbol")
        else -> Validity.Valid(trimmedSymbol)
    }
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