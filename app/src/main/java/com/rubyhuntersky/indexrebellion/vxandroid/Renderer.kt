package com.rubyhuntersky.indexrebellion.vxandroid

import android.support.annotation.LayoutRes
import android.view.View

interface Renderer<V : Any, A : Any, Data : Any> {
    @get:LayoutRes
    val layoutRes: Int

    fun start(view: View, sendAction: (A) -> Unit): Data
    fun end(view: View, data: Data) = Unit
    fun update(vision: V, sendAction: (A) -> Unit, view: View, data: Data): UpdateResult<Data> =
        UpdateResult.Continue(data)
}
