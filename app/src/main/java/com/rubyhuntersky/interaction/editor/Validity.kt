package com.rubyhuntersky.interaction.editor

sealed class Validity {
    object Valid : Validity()
    data class Invalid(val reason: String, val action: String? = null) : Validity()
}