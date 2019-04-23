package com.rubyhuntersky.indexrebellion.vxandroid

abstract class RendererBottomSheetDialogFragment<V : Any, A : Any, Data : Any>(
    private val renderer: Renderer<V, A, Data>
) : InteractionBottomSheetDialogFragment<V, A>(renderer.layoutRes, null) {

    private lateinit var data: Data

    override fun render(vision: V) {
        val oldData = if (::data.isInitialized) data else renderer.start(this.view!!, this::sendAction)
        data = renderer.update(vision, this::sendAction, this.view!!, oldData)
    }

    override fun erase() {
        renderer.end(this.view!!, data)
    }
}