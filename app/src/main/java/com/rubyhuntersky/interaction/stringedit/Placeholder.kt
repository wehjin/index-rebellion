package com.rubyhuntersky.interaction.stringedit

sealed class Placeholder<T : Any> {

    abstract val label: String
    abstract val validValue: T?

    data class Label<T : Any>(
        override val label: String
    ) : Placeholder<T>() {
        override val validValue: T? = null
    }

    data class Seed<T : Any>(
        override val label: String,
        val value: T,
        val isValid: Boolean
    ) : Placeholder<T>() {
        override val validValue: T? = if (isValid) value else null
    }
}