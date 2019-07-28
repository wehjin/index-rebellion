package com.rubyhuntersky.interaction.edit

interface Edit<T : Any, MutationT : Any, SelectionT : Any, NovelT : Novel<T, MutationT, SelectionT>> {

    val label: String
    val seed: Seed<T>?
    val ancient: Ancient<T>?
    val novel: NovelT?

    val enabled: Boolean
        get() = true

    val validValue: T?
        get() {
            val currentNovel = novel
            val currentAncient = ancient
            val currentSeed = seed
            return when {
                currentNovel != null -> currentNovel.validValue
                currentAncient != null -> currentAncient.validValue
                currentSeed != null -> currentSeed.validValue
                else -> null
            }
        }

    val writableValue: T?
        get() = if (isValueFresh || isValueChanged) validValue else null

    private val isValueFresh: Boolean
        get() = ancient == null && (novel != null || seed != null)

    private val isValueChanged: Boolean
        get() {
            val currentNovel = novel
            val currentAncient = ancient
            return currentAncient != null && currentNovel != null && currentNovel.validValue != currentAncient.validValue
        }

    fun setAncient(value: T?): Edit<T, MutationT, SelectionT, NovelT>
}