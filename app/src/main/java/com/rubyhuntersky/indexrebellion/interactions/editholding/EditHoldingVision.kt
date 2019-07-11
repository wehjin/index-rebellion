package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding

sealed class EditHoldingVision {

    private val holdingEdit: HoldingEdit? get() = (this  as? Editing)?.edit

    val symbolEdit get() = holdingEdit?.symbolEdit
    val sizeEdit get() = holdingEdit?.sizeEdit
    val priceEdit get() = holdingEdit?.priceEdit

    object Idle : EditHoldingVision()
    data class Ended(val novelHolding: SpecificHolding?) : EditHoldingVision()
    data class Loading(val specificHolding: SpecificHolding?) : EditHoldingVision()
    data class Editing(val edit: HoldingEdit) : EditHoldingVision()
}