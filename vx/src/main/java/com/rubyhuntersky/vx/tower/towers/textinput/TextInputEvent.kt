package com.rubyhuntersky.vx.tower.towers.textinput

sealed class TextInputEvent<out Topic : Any> {

    abstract val topic: Topic

    data class Changed<out Topic : Any>(
        override val topic: Topic,
        val text: String,
        val selection: IntRange
    ) : TextInputEvent<Topic>()
}