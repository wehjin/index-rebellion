package com.rubyhuntersky.interaction.stringedit

data class StringEdit<T : Any>(
    val label: String,
    val seed: Seed<T>? = null,
    val ancient: Ancient<T>? = null,
    val novel: Novel<T>? = null
) {
    private val validValue: T?
        get() = when {
            novel != null -> novel.validValue
            ancient != null -> ancient.validValue
            seed != null -> seed.validValue
            else -> null
        }

    private val isValueFresh: Boolean
        get() = ancient == null && (novel != null || seed != null)

    private val isValueChanged: Boolean
        get() = ancient != null && novel != null && novel.validValue != ancient.validValue

    val writableValue: T?
        get() = if (isValueFresh || isValueChanged) validValue else null

    fun setNovel(novel: Novel<T>?): StringEdit<T> = copy(novel = novel)
    fun setAncient(value: T?): StringEdit<T> = copy(ancient = value?.let(::Ancient))
}