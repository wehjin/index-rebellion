package com.rubyhuntersky.vx.tower.towers.click

sealed class ClickEvent<Target : Any> {
    abstract val context: Target

    data class Single<Target : Any>(override val context: Target) : ClickEvent<Target>()
}