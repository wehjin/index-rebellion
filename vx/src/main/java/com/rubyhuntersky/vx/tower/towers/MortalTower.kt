package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.Mortal
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MortalTower<Sight : Any, Event : Any>(private val tower: Tower<Sight, Event>) : Tower<Mortal<Sight>, Event> {

    override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Mortal<Sight>, Event> {

        return object : Tower.View<Mortal<Sight>, Event> {


            override fun dequeue() {
                coreView?.dequeue()
            }

            override val events: Observable<Event>
                get() = eventPublish

            override fun setSight(sight: Mortal<Sight>) {
                updates.clear()
                viewHost.drop(viewId, true)
                coreView?.dequeue()
                coreView = when (sight) {
                    is Mortal.Be -> tower.enview(viewHost, viewId).apply { setSight(sight.coil) }
                    is Mortal.NotToBe -> null
                }
                viewHost.drop(viewId, false)
            }

            override fun setHBound(hbound: HBound) {
                edgeHBound = hbound
            }


            override val latitudes: Observable<Latitude>
                get() = latitudeBehavior.distinctUntilChanged()

            override fun setAnchor(anchor: Anchor) {
                edgeAnchor = anchor
            }


            private val updates = CompositeDisposable()
            private val eventPublish: PublishSubject<Event> = PublishSubject.create()
            private val latitudeBehavior: BehaviorSubject<Latitude> = BehaviorSubject.createDefault(Latitude(0))

            private var edgeHBound: HBound? = null
                set(value) {
                    field = value
                    updateCoreHBoundAndAnchor(coreView, value, edgeAnchor)
                }

            private var coreLatitude: Latitude? = null
                set(value) {
                    field = value
                    value?.let {
                        updateCoreAnchor(coreView, edgeAnchor)
                        latitudeBehavior.onNext(it)
                    }
                }

            private var edgeAnchor: Anchor? = null
                set(value) {
                    field = value
                    updateCoreHBoundAndAnchor(coreView, edgeHBound, value)
                }

            private var coreView: Tower.View<Sight, Event>? = null
                set(value) {
                    field = value
                    if (value == null) {
                        coreLatitude = Latitude.ZERO
                    } else {
                        value.latitudes.subscribe { coreLatitude = it }.addTo(updates)
                        value.events.subscribe(eventPublish::onNext).addTo(updates)
                    }
                    updateCoreHBoundAndAnchor(value, edgeHBound, edgeAnchor)
                }

            private fun updateCoreHBoundAndAnchor(
                coreView: Tower.View<Sight, Event>?,
                edgeHBound: HBound?,
                edgeAnchor: Anchor?
            ) {
                if (coreView != null && edgeHBound != null) {
                    coreView.setHBound(edgeHBound)
                }
                updateCoreAnchor(coreView, edgeAnchor)
            }

            private fun updateCoreAnchor(coreView: Tower.View<Sight, Event>?, edgeAnchor: Anchor?) {
                if (coreView != null && edgeAnchor != null) {
                    coreView.setAnchor(edgeAnchor)
                }
            }
        }
    }
}