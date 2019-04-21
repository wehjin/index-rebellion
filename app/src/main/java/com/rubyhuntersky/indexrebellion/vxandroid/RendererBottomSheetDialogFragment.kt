package com.rubyhuntersky.indexrebellion.vxandroid

abstract class RendererBottomSheetDialogFragment<S : Any, V : Any, A : Any>(private val renderer: Renderer<S, V, A>) :
    InteractionBottomSheetDialogFragment<V, A>(renderer.layoutRes, null) {

    private var state = renderer.start()

    override fun render(vision: V) {
        state = renderer.update(state, this.view!!, vision, this::sendAction)
    }
}