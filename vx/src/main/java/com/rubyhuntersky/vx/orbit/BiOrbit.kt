package com.rubyhuntersky.vx.orbit

sealed class BiOrbit {

    abstract val hOrbit: Orbit
    abstract val vOrbit: Orbit

    object Center : BiOrbit() {
        override val hOrbit: Orbit = Orbit.Center
        override val vOrbit: Orbit = Orbit.Center
    }

    object StartCenterLit : BiOrbit() {
        override val hOrbit: Orbit = Orbit.HeadLit
        override val vOrbit: Orbit = Orbit.Center
    }

    object EndCenterLit : BiOrbit() {
        override val hOrbit: Orbit = Orbit.TailLit
        override val vOrbit: Orbit = Orbit.Center
    }
}
