package com.rubyhuntersky.interaction.stringedit

data class StringEdit<T : Any>(
    val placeholder: Placeholder<T>,
    val ancient: Ancient<T>? = null,
    val novel: Novel<T>? = null
) {
    private val validValue: T?
        get() = when {
            novel != null -> novel.validValue
            ancient != null -> ancient.validValue
            else -> placeholder.validValue
        }

    private val isValueSeeded: Boolean
        get() = ancient == null && (novel != null || placeholder is Placeholder.Seed)

    private val isValueChanged: Boolean
        get() = ancient != null && novel != null && novel.validValue != ancient.validValue

    val writableValue: T?
        get() = if (isValueSeeded || isValueChanged) validValue else null
}