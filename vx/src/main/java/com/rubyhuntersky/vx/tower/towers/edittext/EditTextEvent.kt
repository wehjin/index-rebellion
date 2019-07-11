package com.rubyhuntersky.vx.tower.towers.edittext

sealed class EditTextEvent<out Topic : Any> {
    abstract val topic: Topic
}