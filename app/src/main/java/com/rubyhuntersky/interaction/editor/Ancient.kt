package com.rubyhuntersky.interaction.editor

data class Ancient<T : Any>(
    val value: T,
    val isValid: Boolean
) {
    internal fun asEditingResult() = EditorResult(value, isValid)
}