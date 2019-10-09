package com.rubyhuntersky.vx.common

import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.Share

sealed class Span(val orbit: Orbit = Orbit.Center) {

    abstract fun realize(length: Int): Int

    data class Absolute(val count: Int) : Span() {
        override fun realize(length: Int): Int = count
    }

    data class Relative(val fraction: Float) : Span() {
        override fun realize(length: Int): Int = (length * fraction).toInt()
    }

    object None : Span() {
        override fun realize(length: Int): Int = 0
    }

    companion object {
        val HALF = Relative(0.500f)
        val THIRD = Relative(0.333f)
        val FIFTH = Relative(1f / 5)
        val SIXTH = Relative(1f / 6)
        val EIGHTH = Relative(1f / 8)
    }

    operator fun <Sight : Any, Event : Any> get(tower: Tower<Sight, Event>) = Share(this, tower)
}