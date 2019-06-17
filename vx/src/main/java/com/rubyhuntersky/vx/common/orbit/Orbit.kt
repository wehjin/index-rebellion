package com.rubyhuntersky.vx.common.orbit

sealed class Orbit {

    abstract val pole: Float
    abstract val swing: Float

    object HeadLit : Orbit() {
        override val pole: Float = 0.0f
        override val swing: Float = 0.0f
    }

    object TailLit : Orbit() {
        override val pole: Float = 1.0f
        override val swing: Float = 1.0f
    }

    object Center : Orbit() {
        override val pole: Float = 0.5f
        override val swing: Float = 0.5f
    }

    object HeadDim : Orbit() {
        override val pole: Float = 0.0f
        override val swing: Float = 1.0f
    }

    object TailDim : Orbit() {
        override val pole: Float = 1.0f
        override val swing: Float = 0.0f
    }

    class Custom(
        override val pole: Float,
        override val swing: Float
    ) : Orbit()
}
