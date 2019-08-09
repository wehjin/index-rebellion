package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

fun <Sight : Any, Event : Any> Tower<Sight, Event>.shareEnd(
    span: Span,
    tower: Tower<Sight, Event>
): Tower<Sight, Event> {
    return this.plusHShare(HShare.End(span, tower))
}

fun <Sight : Any, Event : Any> Tower<Sight, Event>.plusHShare(hShare: HShare<Sight, Event>): Tower<Sight, Event> {
    val core = this
    val alt = hShare.tower
    val span = hShare.span
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> {
            val coreView = core.enview(viewHost, viewId.extend(0))
            val altView = alt.enview(viewHost, viewId.extend(1))
            return object : Tower.View<Sight, Event> {
                override fun dequeue() {
                    latitudeWatchers.clear()
                    coreView.dequeue()
                    altView.dequeue()
                }

                override val events: Observable<Event>
                    get() = Observable.merge(coreView.events, altView.events)

                override fun setSight(sight: Sight) {
                    coreView.setSight(sight)
                    altView.setSight(sight)
                }

                override fun setHBound(hbound: HBound) {
                    edgeBound = hbound
                    val (coreBound, altBound) = hShare.hostGuestBounds(hbound)
                    coreView.setHBound(coreBound)
                    altView.setHBound(altBound)
                }

                private var edgeBound: HBound? = null

                override val latitudes: Observable<Latitude>
                    get() = edgeLatitude.distinctUntilChanged()

                private var edgeLatitude =
                    BehaviorSubject.createDefault(Latitude(0))

                override fun setAnchor(anchor: Anchor) {
                    edgeAnchor = anchor
                    updateCoreAnchors(subLatitudes, edgeAnchor)
                }

                private var edgeAnchor: Anchor? = null

                private fun updateCoreAnchors(subLatitudes: List<Latitude>, edgeAnchor: Anchor?): Latitude {
                    val maxLatitude = subLatitudes.fold(Latitude(0), Latitude::max)
                    if (edgeAnchor != null) {
                        val edgeHeight = maxLatitude.height
                        val edgeCeiling = edgeAnchor.toCeiling(edgeHeight)
                        val coreAnchor = Anchor(
                            position = edgeCeiling + (span.orbit.pole * edgeHeight).toInt(),
                            placement = span.orbit.swing
                        )
                        subViews.forEach { it.setAnchor(coreAnchor) }
                    }
                    return maxLatitude
                }

                private val subViews = listOf(coreView, altView)
                private val subLatitudes = subViews.map { Latitude(0) }.toMutableList()
                private val latitudeWatchers = CompositeDisposable()

                init {
                    subViews.forEachIndexed { index, subView ->
                        subView.latitudes
                            .subscribe { latitude ->
                                subLatitudes[index] = latitude
                                val maxLatitude = updateCoreAnchors(subLatitudes, edgeAnchor)
                                edgeLatitude.onNext(maxLatitude)
                            }.addTo(latitudeWatchers)
                    }
                }
            }
        }
    }
}
