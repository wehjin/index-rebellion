package com.rubyhuntersky.interaction.edit

import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent

data class StringChange(
    val value: String,
    val selection: IntRange
)

fun TextInputEvent<*>.toStringChange(): StringChange? =
    if (this is TextInputEvent.Changed) StringChange(text, selection) else null
