package com.rubyhuntersky.interaction.edit

data class Seed<T : Any>(
    val value: T,
    val isValid: Boolean
) {
    val validValue: T? = if (isValid) value else null
}
