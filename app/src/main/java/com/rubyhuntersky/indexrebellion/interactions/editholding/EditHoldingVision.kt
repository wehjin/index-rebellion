package com.rubyhuntersky.indexrebellion.interactions.editholding

sealed class EditHoldingVision {

    val holdingEdit: HoldingEdit? get() = (this  as? Editing)?.edit
    val symbolEdit get() = holdingEdit?.symbolEdit
    val sizeEdit get() = holdingEdit?.sizeEdit
    val priceEdit get() = holdingEdit?.priceEdit

    object Idle : EditHoldingVision()
    object Ended : EditHoldingVision()
    data class Editing(val edit: HoldingEdit) : EditHoldingVision()
}