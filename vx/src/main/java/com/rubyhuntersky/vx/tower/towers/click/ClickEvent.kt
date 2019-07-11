package com.rubyhuntersky.vx.tower.towers.click

sealed class ClickEvent<Topic : Any> {
    abstract val topic: Topic

    data class Single<Topic : Any>(override val topic: Topic) : ClickEvent<Topic>()
}