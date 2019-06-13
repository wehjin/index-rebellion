package com.rubyhuntersky.vx.coop.additions

sealed class Span {

    abstract fun realize(length: Int): Int

    data class Absolute(val count: Int) : Span() {
        override fun realize(length: Int): Int = count
    }

    data class Relative(val fraction: Float) : Span() {
        override fun realize(length: Int): Int = (length * fraction).toInt()
    }
}