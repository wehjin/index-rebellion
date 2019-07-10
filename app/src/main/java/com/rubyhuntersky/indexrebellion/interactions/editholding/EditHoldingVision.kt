package com.rubyhuntersky.indexrebellion.interactions.editholding

sealed class EditHoldingVision {
    object Idle : EditHoldingVision()
    object Ended : EditHoldingVision()
    data class Editing(val unit: Unit) : EditHoldingVision()
}