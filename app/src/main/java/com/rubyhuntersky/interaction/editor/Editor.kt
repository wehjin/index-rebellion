package com.rubyhuntersky.interaction.editor

data class Editor<T : Any>(
    val isStrict: Boolean = true,
    val placeholder: Placeholder<T>,
    val ancient: Ancient<T>? = null,
    val novel: Novel<T>? = null
) {
    private val anyResult: EditorResult<T>?
        get() = novel?.asEditingResult() ?: ancient?.asEditingResult() ?: when (placeholder) {
            is Placeholder.Label -> null
            is Placeholder.Seed -> placeholder.asEditingResult()
        }

    private val hasFreshResult: Boolean
        get() = ancient == null && (novel != null || placeholder is Placeholder.Seed)

    private val hasChangeResult: Boolean
        get() = ancient != null && novel != null && novel.value != ancient.value

    private val permissiveResult
        get() = when {
            hasFreshResult || hasChangeResult -> anyResult
            else -> null
        }

    val writableValue: T?
        get() = permissiveResult?.let {
            if (isStrict) {
                if (it.isValid) it.value else null
            } else {
                it.value
            }
        }
}