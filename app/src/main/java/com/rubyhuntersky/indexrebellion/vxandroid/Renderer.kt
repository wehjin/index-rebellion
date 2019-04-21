package com.rubyhuntersky.indexrebellion.vxandroid

import android.support.annotation.LayoutRes
import android.view.View

data class Renderer<S : Any, V : Any, A : Any>(
    @LayoutRes val layoutRes: Int,
    val start: () -> S,
    val update: (state: S, contentView: View, vision: V, sendAction: (A) -> Unit) -> S = { state, _, _, _ -> state },
    val end: ((state: S) -> Unit)? = null
)
