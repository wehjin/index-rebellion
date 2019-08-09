package com.rubyhuntersky.vx.tower.additions.pad

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

operator fun <Sight : Any, Event : Any> Tower<Sight, Event>.plus(pad: VPad): Tower<Sight, Event> = this.plusVPad(pad)

fun <Sight : Any, Event : Any> Tower<Sight, Event>.plusVPad(pad: VPad): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> =
            object : Tower.View<Sight, Event> {
                
                override fun drop() {
                    latitudeAnchorUpdates.clear()
                    coreView.drop()
                }

                private val coreView = core.enview(viewHost, viewId)
                private val edgeLatitudes: PublishSubject<Height> = PublishSubject.create()
                private val edgeAnchors: PublishSubject<Anchor> = PublishSubject.create()
                private val latitudeAnchorUpdates = CompositeDisposable()

                init {
                    coreView.latitudes.subscribe {
                        edgeLatitudes.onNext(Height(it.dips + pad.ceilingHeight + pad.floorHeight))
                    }.addTo(latitudeAnchorUpdates)
                    combineLatest(
                        edgeLatitudes, edgeAnchors,
                        BiFunction { height: Height, anchor: Anchor -> Pair(height, anchor) }
                    ).subscribe { (latitude, anchor) ->
                        val edgeVBound = anchor.toVBound(latitude.dips)
                        coreView.setAnchor(Anchor(edgeVBound.ceiling + pad.ceilingHeight, 0f))
                    }.addTo(latitudeAnchorUpdates)
                }

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Height> = edgeLatitudes.distinctUntilChanged()
                override fun setAnchor(anchor: Anchor) = edgeAnchors.onNext(anchor)
            }
    }
}