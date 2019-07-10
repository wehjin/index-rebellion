package com.rubyhuntersky.interaction.stringedit

sealed class Validity<out T : Any> {
    data class Valid<out T : Any>(val value: T) : Validity<T>()
    data class Invalid<out T : Any>(val reason: String, val action: String? = null) : Validity<T>()
}