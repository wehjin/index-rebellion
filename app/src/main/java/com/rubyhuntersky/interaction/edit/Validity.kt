package com.rubyhuntersky.interaction.edit

sealed class Validity<out T : Any, out MutationT : Any> {

    val isValid: Boolean
        get() = this is Valid<T, MutationT>

    data class Valid<out T : Any, out MutationT : Any>(
        val value: T
    ) : Validity<T, MutationT>()

    data class Invalid<out T : Any, out MutationT : Any>(
        val value: MutationT,
        val reason: String,
        val action: String? = null
    ) : Validity<T, MutationT>()
}

