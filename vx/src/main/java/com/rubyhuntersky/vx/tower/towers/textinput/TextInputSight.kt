package com.rubyhuntersky.vx.tower.towers.textinput

data class TextInputSight<out Topic : Any>(
    val topic: Topic,
    val text: String,
    val selection: IntRange = IntRange(text.length, text.length - 1),
    val hint: String = "",
    val label: String = "",
    val error: String = ""
)
