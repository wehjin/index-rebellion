package com.rubyhuntersky.interaction.editor

internal data class EditorResult<T : Any>(val value: T, val isValid: Boolean)