package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendFloor(tower: Tower<Sight, Event>) =
    plusAugment(HAugment.Floor(tower))

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendFloors(vararg floors: Tower<Sight, Event>) =
    floors.fold(this, Tower<Sight, Event>::extendFloor)

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendCeiling(tower: Tower<Sight, Event>) =
    plusAugment(HAugment.Ceiling(tower))

fun <Sight : Any, Event : Any> Tower<Sight, Event>.plusAugment(augment: HAugment<Sight, Event>): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> =
            object : Tower.View<Sight, Event> {
                
                private val views = listOf(augment.ceilingTower, core, augment.floorTower)
                    .mapIndexed { index, tower -> tower.enview(viewHost, id.extend(index)) }

                private val fullLatitudes = BehaviorSubject.createDefault(Latitude(0))
                private val subviewLatitudes = views.map { Latitude(0) }.toMutableList()
                private val subviewLatitudeChangeWatchers = CompositeDisposable()
                private var edgeAnchor: Anchor? = null

                private fun updateSubviewAnchors(): Latitude {
                    val fullLatitude = subviewLatitudes.fold(Latitude(0), Latitude::plus)
                    edgeAnchor?.let { edgeAnchor ->
                        val offset0 = 0
                        val offset1 = offset0 + subviewLatitudes[0].height
                        val offset2 = offset1 + subviewLatitudes[1].height
                        val coreOffsets = listOf(offset0, offset1, offset2)
                        coreOffsets.forEachIndexed { index, coreOffset ->
                            val coreHeight = subviewLatitudes[index].height
                            val coreAnchor = edgeAnchor.edgeToCore(fullLatitude.height, coreHeight, coreOffset)
                            views[index].setAnchor(coreAnchor)
                        }
                    }
                    return fullLatitude
                }

                init {
                    views.forEachIndexed { index, view ->
                        view.latitudes.subscribe { latitude ->
                            subviewLatitudes[index] = latitude
                            val fullLatitude = updateSubviewAnchors()
                            fullLatitudes.onNext(fullLatitude)
                        }.addTo(subviewLatitudeChangeWatchers)
                    }
                }

                override val events: Observable<Event>
                    get() = Observable.merge(views.map { it.events })

                override fun setSight(sight: Sight) = views.forEach { it.setSight(sight) }
                override fun setHBound(hbound: HBound) = views.forEach { it.setHBound(hbound) }
                override val latitudes: Observable<Latitude> = fullLatitudes.distinctUntilChanged()
                override fun setAnchor(anchor: Anchor) {
                    edgeAnchor = anchor
                    updateSubviewAnchors()
                }
            }
    }
}
