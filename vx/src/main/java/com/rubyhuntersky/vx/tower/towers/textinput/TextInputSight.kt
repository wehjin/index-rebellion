package com.rubyhuntersky.vx.tower.towers.textinput

import com.rubyhuntersky.vx.tower.towers.InputType

data class TextInputSight<out Topic : Any>(
    val type: InputType,
    val topic: Topic,
    val text: String,
    val selection: IntRange = IntRange(text.length, text.length - 1),
    val hint: String = "",
    val label: String = "",
    val error: String = ""
)
