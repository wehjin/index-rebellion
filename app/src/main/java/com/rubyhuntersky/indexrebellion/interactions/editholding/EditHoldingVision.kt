package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.interaction.stringedit.StringEdit
import java.math.BigDecimal

sealed class EditHoldingVision {
    object Idle : EditHoldingVision()
    object Ended : EditHoldingVision()
    data class Editing(val sizeEdit: StringEdit<BigDecimal>) : EditHoldingVision()
}