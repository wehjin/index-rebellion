package com.rubyhuntersky.vx.tower.towers.textinput

sealed class TextInputEvent<out Topic : Any> {

    abstract val topic: Topic

    fun <T : Any, MaybeTopic : Any> mapTopic(topic: MaybeTopic, mapper: (TextInputEvent<Topic>) -> T?): T? =
        if (this.topic == topic) mapper(this) else null

    data class Changed<out Topic : Any>(
        override val topic: Topic,
        val text: String,
        val selection: IntRange
    ) : TextInputEvent<Topic>()
}