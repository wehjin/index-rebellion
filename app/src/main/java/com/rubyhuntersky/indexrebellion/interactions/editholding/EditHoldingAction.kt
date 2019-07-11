package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding

sealed class EditHoldingAction {
    data class Ignore(val ignore: Any) : EditHoldingAction()
    object End : EditHoldingAction()
    data class Start(val holding: SpecificHolding?) : EditHoldingAction()
    data class SetSize(val change: Pair<String, IntRange>) : EditHoldingAction()
}