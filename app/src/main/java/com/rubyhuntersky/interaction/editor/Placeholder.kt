package com.rubyhuntersky.interaction.editor

sealed class Placeholder<T : Any> {

    abstract val label: String

    data class Label<T : Any>(
        override val label: String
    ) : Placeholder<T>()

    data class Seed<T : Any>(
        val value: T,
        val isValid: Boolean,
        override val label: String
    ) : Placeholder<T>() {
        internal fun asEditingResult() = EditorResult(value, isValid)
    }
}