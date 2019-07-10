package com.rubyhuntersky.interaction.stringedit

data class Seed<T : Any>(
    val value: T,
    val isValid: Boolean
) {
    val validValue: T? = if (isValid) value else null
}
