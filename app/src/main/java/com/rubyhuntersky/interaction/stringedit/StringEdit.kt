package com.rubyhuntersky.interaction.stringedit

import com.rubyhuntersky.interaction.edit.Ancient
import com.rubyhuntersky.interaction.edit.Edit
import com.rubyhuntersky.interaction.edit.Seed
import com.rubyhuntersky.interaction.edit.StringNovel

data class StringEdit<T : Any>(
    override val label: String,
    override val seed: Seed<T>? = null,
    override val ancient: Ancient<T>? = null,
    override val novel: StringNovel<T>? = null,
    override val enabled: Boolean = true
) : Edit<T, String, IntRange, StringNovel<T>> {

    fun setNovel(novel: StringNovel<T>?): StringEdit<T> = copy(novel = novel)
    override fun setAncient(value: T?): StringEdit<T> = copy(ancient = value?.let(::Ancient))
}
