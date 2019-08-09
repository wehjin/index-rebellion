package com.rubyhuntersky.vx.common

data class Height(val dips: Int) {
    operator fun plus(other: Height) = Height(dips + other.dips)
    fun max(other: Height) = if (dips > other.dips) this else other

    companion object {
        val ZERO = Height(0)
    }
}