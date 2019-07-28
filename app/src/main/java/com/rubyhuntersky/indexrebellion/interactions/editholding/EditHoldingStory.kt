package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory.Action
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory.Vision
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDrifts
import com.rubyhuntersky.indexrebellion.spirits.genies.writedrift.WriteDrift
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionCompanion
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.edit.StringNovel
import com.rubyhuntersky.interaction.edit.Validity
import java.math.BigDecimal

class EditHoldingStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    sealed class Vision {

        val symbolEdit get() = (this  as? Editing)?.holdingEdit?.symbolEdit
        val sizeEdit get() = (this  as? Editing)?.holdingEdit?.sizeEdit
        val priceEdit get() = (this  as? Editing)?.holdingEdit?.priceEdit

        object Idle : Vision()
        data class Loading(val holdingEditType: HoldingEditType) : Vision()
        data class Editing(val holdingEdit: HoldingEdit) : Vision()
        data class Ended(val novelHolding: SpecificHolding?) : Vision()
    }

    sealed class Action {
        data class Start(val editType: HoldingEditType) : Action()
        data class Ignore(val ignore: Any) : Action()
        data class Load(val drift: Drift) : Action()
        data class SetSize(val change: Pair<String, IntRange>) : Action()
        data class SetSymbol(val change: Pair<String, IntRange>) : Action()
        data class SetPrice(val change: Pair<String, IntRange>) : Action()
        object Write : Action()
        object End : Action()
    }

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = EditHoldingStory::class.java.simpleName
    }
}

private fun start(): Vision = Vision.Idle

private fun isEnding(maybe: Any?): Boolean = maybe is Vision.Ended

private const val DRIFTS_WISH = "drifts"
private const val WRITE_DRIFT_WISH = "write-drift"

@Suppress("IntroduceWhenSubject")
private fun revise(vision: Vision, action: Action): Revision<Vision, Action> = when {
    action is Action.End -> {
        Revision(Vision.Ended(null), Wish.none(DRIFTS_WISH), Wish.none(WRITE_DRIFT_WISH))
    }
    action is Action.Ignore -> {
        println(addTag("IGNORED: ${action.ignore} VISION: $vision"))
        Revision(vision)
    }
    vision is Vision.Idle && action is Action.Start -> {
        val drifts = ReadDrifts.toWish<ReadDrifts, Action>(
            name = DRIFTS_WISH,
            onResult = Action::Load,
            onError = Action::Ignore
        )
        Revision(Vision.Loading(action.editType), drifts)
    }
    vision is Vision.Loading && action is Action.Load -> {
        val edit = HoldingEdit(vision.holdingEditType).setDrift(action.drift)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.Load -> {
        val edit = vision.holdingEdit.setDrift(action.drift)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetSize -> {
        val size = action.change.first
        val novel = size.toNovel(action.change.second, ::sizeValidity)
        val edit = vision.holdingEdit.setSizeNovel(novel)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetSymbol -> {
        val symbol = action.change.first
        val novel = symbol.toNovel(action.change.second, ::symbolValidity)
        val edit = vision.holdingEdit.setSymbolNovel(novel)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.SetPrice -> {
        val price = action.change.first
        val novel = price.toNovel(action.change.second, ::priceValidity)
        val edit = vision.holdingEdit.setPriceNovel(novel)
        Revision(Vision.Editing(edit))
    }
    vision is Vision.Editing && action is Action.Write -> {
        vision.holdingEdit.writableResult
            ?.let { (drift, holding) ->
                val writeDrift = WriteDrift(drift).toWish<WriteDrift, Action>(
                    name = WRITE_DRIFT_WISH,
                    onResult = Action::Ignore,
                    onError = Action::Ignore
                )
                Revision(Vision.Ended(holding), writeDrift)
            }
            ?: Revision(vision)
    }
    else -> Revision<Vision, Action>(vision).also {
        System.err.println(addTag("BAD REVISION: $action, $vision"))
    }
}

private fun <T : Any> String.toNovel(
    selection: IntRange,
    toValidity: (String) -> Validity<T, String>
): StringNovel<T>? =
    if (isBlank()) null
    else StringNovel(this, toValidity(this), selection)

private fun priceValidity(price: String): Validity<CashAmount, String> {
    val cashPrice = price.toBigDecimalOrNull()?.let(::CashAmount)
    return when {
        cashPrice != null && cashPrice > CashAmount.ZERO -> Validity.Valid(cashPrice)
        else -> Validity.Invalid(price, "Must be positive")
    }
}

private fun symbolValidity(symbol: String): Validity<String, String> {
    val trimmedSymbol = symbol.trim()
    return when {
        trimmedSymbol.isBlank() -> Validity.Invalid(symbol, "No a symbol")
        else -> Validity.Valid(trimmedSymbol)
    }
}

private fun sizeValidity(string: String): Validity<BigDecimal, String> =
    string.toDoubleOrNull()
        ?.let(BigDecimal::valueOf)
        ?.let {
            if (it >= BigDecimal.ZERO) Validity.Valid<BigDecimal, String>(it)
            else Validity.Invalid<BigDecimal, String>(string, "Must be positive")
        }
        ?: Validity.Invalid(string, "Invalid double")

private fun addTag(message: String): String = "${EditHoldingStory.groupId} $message"