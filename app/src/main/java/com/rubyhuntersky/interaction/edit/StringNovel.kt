package com.rubyhuntersky.interaction.edit

data class StringNovel<out T : Any>(
    override val component: String,
    override val validity: Validity<T, String>,
    override val selection: IntRange = component.length until component.length
) : Novel<T, String, IntRange>
