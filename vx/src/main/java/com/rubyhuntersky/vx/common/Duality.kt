package com.rubyhuntersky.vx.common

sealed class Duality<out T : Any, out U : Any> {

    val isYin: Boolean get() = this is Yin
    val isYang: Boolean get() = this is Yang

    data class Yin<out T : Any>(val y: T) : Duality<T, Nothing>() {
        override fun toString(): String = "Yin($y)"
    }

    data class Yang<out U : Any>(val y: U) : Duality<Nothing, U>() {
        override fun toString(): String = "Yang($y)"
    }

    fun <V : Any> mapYin(map: (T) -> V): Duality<V, U> = when (this) {
        is Yin -> Yin(map(y))
        is Yang -> this
    }

    fun <V : Any> mapYang(map: (U) -> V): Duality<T, V> = when (this) {
        is Yin -> this
        is Yang -> Yang(map(y))
    }
}
