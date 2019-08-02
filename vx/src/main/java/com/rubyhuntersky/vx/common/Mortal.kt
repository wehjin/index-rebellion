package com.rubyhuntersky.vx.common

sealed class Mortal<out T : Any> {

    abstract fun toCoil(): T?

    @JvmSynthetic
    abstract operator fun component1(): T?

    data class Be<out T : Any>(val coil: T) : Mortal<T>() {
        override fun toCoil(): T = coil
        override fun toString(): String = "Be($coil)"
    }

    object NotToBe : Mortal<Nothing>() {
        override fun toCoil(): Nothing? = null
        override fun component1(): Nothing? = null
    }
}
