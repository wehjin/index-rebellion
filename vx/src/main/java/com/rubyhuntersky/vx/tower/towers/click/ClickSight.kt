package com.rubyhuntersky.vx.tower.towers.click

data class ClickSight<ClickContext : Any>(
    val label: String,
    val clickContext: ClickContext
)