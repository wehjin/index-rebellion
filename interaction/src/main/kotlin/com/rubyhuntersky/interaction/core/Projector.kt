package com.rubyhuntersky.interaction.core

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class Projector<V : Any, A : Any>(
    private val interaction: Interaction<V, A>,
    private val observeOn: Scheduler,
    private val log: ((vision: V) -> Unit)? = null
) {

    private val composite = CompositeDisposable()
    private var render: ((vision: V, sendAction: (A) -> Unit) -> Unit)? = null

    fun <V2> addComponent(component: Component<V, V2, A>): Projector<V, A> {
        components.add(component)
        return this
    }

    private val components = mutableListOf<Component<V, *, A>>()

    interface Component<V, V2, A> {
        fun convert(vision: V): V2?
        fun render(vision: V2, sendAction: (A) -> Unit)
        fun erase() {}
        fun subRender(vision: V, sendAction: (A) -> Unit) =
            convert(vision)?.let { revision: V2 ->
                true.also { render(revision, sendAction) }
            } ?: false.also { erase() }
    }

    fun fallback(render: (vision: V, sendAction: (A) -> Unit) -> Unit): Projector<V, A> {
        this.render = render
        return this
    }

    fun start() {
        interaction.visionStream
            .observeOn(observeOn)
            .subscribe { vision ->
                log?.invoke(vision)
                val rendered =
                    components.fold(false) { rendered, component ->
                        if (!rendered) {
                            component.subRender(vision, interaction::sendAction)
                        } else {
                            true
                        }
                    }
                if (!rendered) {
                    render!!.invoke(vision, interaction::sendAction)
                }
            }.addTo(composite)
    }

    fun stop() {
        composite.clear()
    }
}