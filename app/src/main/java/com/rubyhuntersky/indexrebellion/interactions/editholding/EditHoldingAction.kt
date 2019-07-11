package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingAction as Action

sealed class EditHoldingAction {
    data class Ignore(val ignore: Any) : Action()
    object End : Action()
    data class Start(val holding: SpecificHolding?) : Action()
    data class SetSize(val change: Pair<String, IntRange>) : Action()
    data class SetSymbol(val change: Pair<String, IntRange>) : Action()
    data class SetPrice(val change: Pair<String, IntRange>) : Action()
}