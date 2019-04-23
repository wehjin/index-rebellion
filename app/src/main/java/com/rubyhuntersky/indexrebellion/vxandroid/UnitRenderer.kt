package com.rubyhuntersky.indexrebellion.vxandroid

import android.view.View

interface UnitRenderer<V : Any, A : Any> : Renderer<V, A, Unit> {
    fun update(vision: V, sendAction: (A) -> Unit, view: View)

    override fun start(view: View, sendAction: (A) -> Unit) = Unit
    override fun update(vision: V, sendAction: (A) -> Unit, view: View, data: Unit) =
        update(vision, sendAction, view)
}