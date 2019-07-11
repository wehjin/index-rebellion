package com.rubyhuntersky.interaction.stringedit

data class Novel<T : Any>(
    val string: String,
    val validity: Validity<T>,
    val selection: IntRange = string.length until string.length
) {
    val validValue: T? = when (validity) {
        is Validity.Valid -> validity.value
        is Validity.Invalid -> null
    }
}