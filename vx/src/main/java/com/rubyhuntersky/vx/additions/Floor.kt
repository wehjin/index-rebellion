package com.rubyhuntersky.vx.additions

import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.ViewHost
import com.rubyhuntersky.vx.ViewId
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject


data class Floor<A : Any, B : Any, C : Any, Ev : Any>(
    val dash: Dash<B, Ev>,
    val onContent: (C) -> Pair<A, B>
)

operator fun <A : Any, B : Any, C : Any, Ev : Any> Dash<A, Ev>.plus(floor: Floor<A, B, C, Ev>): Dash<C, Ev> =
    object : Dash<C, Ev> {
        override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<C, Ev> = object : Dash.View<C, Ev> {
            private val viewA = this@plus.enview(viewHost, id.extend(0))
            private val viewB = floor.dash.enview(viewHost, id.extend(1))
            private val heights = Observable.combineLatest(viewA.latitudes, viewB.latitudes, sumLatitudes)
            private val anchorBehavior = BehaviorSubject.createDefault(Anchor(0, 0f))
            private val sizeAnchors = Observable.combineLatest(heights, anchorBehavior, toSizeAnchor)
            private val composite = CompositeDisposable()

            init {
                sizeAnchors.distinctUntilChanged()
                    .subscribe { sizeAnchor ->
                        val ceilFloor = sizeAnchor.anchor.toBounds(sizeAnchor.size)
                        viewA.setAnchor(Anchor(ceilFloor.first, 0f))
                        viewB.setAnchor(Anchor(ceilFloor.second, 1f))
                    }.addTo(composite)
            }

            override fun setLimit(limit: Dash.Limit) {
                viewA.setLimit(limit)
                viewB.setLimit(limit)
            }

            override val latitudes: Observable<Dash.Latitude> get() = heights.map { Dash.Latitude(it) }

            override fun setAnchor(anchor: Anchor) {
                anchorBehavior.onNext(anchor)
            }

            override fun setContent(content: C) {
                val ab = floor.onContent(content)
                viewA.setContent(ab.first)
                viewB.setContent(ab.second)
            }

            override val events: Observable<Ev> get() = viewA.events.mergeWith(viewB.events)
        }
    }

private data class SizeAnchor(val size: Int, val anchor: Anchor)

private val toSizeAnchor = BiFunction(::SizeAnchor)
private val sumLatitudes = BiFunction<Dash.Latitude, Dash.Latitude, Int> { a, b -> a.height + b.height }
