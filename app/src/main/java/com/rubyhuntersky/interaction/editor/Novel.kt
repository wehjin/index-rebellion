package com.rubyhuntersky.interaction.editor

data class Novel<T : Any>(
    val value: T,
    val validity: Validity
) {
    internal fun asEditingResult() = EditorResult(value, validity is Validity.Valid)
}