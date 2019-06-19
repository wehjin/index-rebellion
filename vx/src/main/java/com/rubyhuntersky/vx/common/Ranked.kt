package com.rubyhuntersky.vx.common

data class Ranked<out T : Any>(
    val value: T,
    val rank: Int
)