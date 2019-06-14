package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

data class Bottom<A : Any, B : Any, C : Any, Ev : Any>(
    val tower: Tower<B, Ev>,
    val onSight: (C) -> Pair<A, B>
)

operator fun <A : Any, B : Any, C : Any, Ev : Any> Tower<A, Ev>.plus(bottom: Bottom<A, B, C, Ev>): Tower<C, Ev> =
    object : Tower<C, Ev> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<C, Ev> = object : Tower.View<C, Ev> {
            private val viewA = this@plus.enview(viewHost, id.extend(0))
            private val viewB = bottom.tower.enview(viewHost, id.extend(1))
            private val heights = Observable.combineLatest(viewA.latitudes, viewB.latitudes, sumLatitudes)
            private val anchorBehavior = BehaviorSubject.createDefault(Anchor(0, 0f))
            private val sizeAnchors = Observable.combineLatest(heights, anchorBehavior, toSizeAnchor)
            private val composite = CompositeDisposable()

            init {
                sizeAnchors.distinctUntilChanged()
                    .subscribe { sizeAnchor ->
                        val ceilFloor = sizeAnchor.anchor.toBound(sizeAnchor.size)
                        viewA.setAnchor(Anchor(ceilFloor.first, 0f))
                        viewB.setAnchor(Anchor(ceilFloor.second, 1f))
                    }.addTo(composite)
            }

            override fun setHBound(hbound: HBound) {
                viewA.setHBound(hbound)
                viewB.setHBound(hbound)
            }

            override val latitudes: Observable<Tower.Latitude> get() = heights.map { Tower.Latitude(it) }

            override fun setAnchor(anchor: Anchor) {
                anchorBehavior.onNext(anchor)
            }

            override fun setSight(sight: C) {
                val ab = bottom.onSight(sight)
                viewA.setSight(ab.first)
                viewB.setSight(ab.second)
            }

            override val events: Observable<Ev> get() = viewA.events.mergeWith(viewB.events)
        }
    }

private val sumLatitudes = BiFunction<Tower.Latitude, Tower.Latitude, Int> { a, b -> a.height + b.height }
