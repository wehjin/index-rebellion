package com.rubyhuntersky.vx.common

data class Latitude(val height: Int) {
    operator fun plus(other: Latitude) =
        Latitude(height + other.height)
    fun max(other: Latitude) = if (height > other.height) this else other
}