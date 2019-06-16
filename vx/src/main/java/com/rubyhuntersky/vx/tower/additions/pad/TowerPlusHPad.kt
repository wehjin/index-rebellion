package com.rubyhuntersky.vx.tower.additions.pad

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

operator fun <Sight : Any, Event : Any> Tower<Sight, Event>.plus(pad: HPad): Tower<Sight, Event> = this.plusPad(pad)

fun <Sight : Any, Event : Any> Tower<Sight, Event>.plusPad(pad: HPad): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> =
            object : Tower.View<Sight, Event> {
                private val view = core.enview(viewHost, id)
                private val fullLatitudes = BehaviorSubject.createDefault(Latitude(0))
                private var coreLatitude = Latitude(0)
                private val coreLatitudeChangeWatcher = CompositeDisposable()
                private var activeAnchor: Anchor? = null

                private fun updateSubviewAnchors(): Latitude {
                    val fullLatitude =
                        Latitude(pad.ceilingHeight + coreLatitude.height + pad.floorHeight)
                    activeAnchor?.let { activeAnchor ->
                        val corePosition = activeAnchor.position + pad.ceilingHeight
                        val coreHeight = coreLatitude.height
                        val corePlacement = if (coreHeight == 0) {
                            0f
                        } else {
                            activeAnchor.placement * fullLatitude.height.toFloat() / coreHeight.toFloat()
                        }
                        view.setAnchor(Anchor(corePosition, corePlacement))
                    }
                    return fullLatitude
                }

                init {
                    view.latitudes.subscribe { latitude ->
                        coreLatitude = latitude
                        val fullLatitude = updateSubviewAnchors()
                        fullLatitudes.onNext(fullLatitude)
                    }.addTo(coreLatitudeChangeWatcher)
                }

                override val events: Observable<Event> get() = view.events
                override fun setSight(sight: Sight) = view.setSight(sight)
                override fun setHBound(hbound: HBound) = view.setHBound(hbound)
                override val latitudes: Observable<Latitude> = fullLatitudes.distinctUntilChanged()

                override fun setAnchor(anchor: Anchor) {
                    activeAnchor = anchor
                    updateSubviewAnchors()
                }
            }
    }
}