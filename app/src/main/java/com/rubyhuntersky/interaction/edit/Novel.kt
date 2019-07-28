package com.rubyhuntersky.interaction.edit

interface Novel<out T : Any, out ComponentT : Any, out SelectionT : Any> {

    val component: ComponentT
    val validity: Validity<T, ComponentT>
    val selection: SelectionT

    val validValue: T?
        get() = when (val currentValidity = validity) {
            is Validity.Valid -> currentValidity.value
            is Validity.Invalid -> null
        }
}