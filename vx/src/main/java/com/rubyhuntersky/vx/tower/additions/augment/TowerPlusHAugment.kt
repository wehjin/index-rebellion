package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

fun <Sight : Any, Event : Any> Tower<Sight, Event>.plusAugment(augment: HAugment<Sight, Event>): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> =
            object : Tower.View<Sight, Event> {
                private val views =
                    listOf(augment.ceilingTower, core, augment.floorTower)
                        .mapIndexed { index, tower ->
                            tower.enview(viewHost, id.extend(index))
                        }
                private val fullLatitudes = BehaviorSubject.createDefault(Tower.Latitude(0))
                private val subviewLatitudes = views.map { Tower.Latitude(0) }.toMutableList()
                private val subviewLatitudeChangeWatchers = CompositeDisposable()
                private var activeAnchor: Anchor? = null

                private fun updateSubviewAnchors(): Tower.Latitude {
                    val fullLatitude = subviewLatitudes.fold(Tower.Latitude(0), Tower.Latitude::plus)
                    activeAnchor?.let { activeAnchor ->
                        val position0 = activeAnchor.position
                        val position1 = position0 + subviewLatitudes[0].height
                        val position2 = position1 + subviewLatitudes[1].height
                        val positions = listOf(position0, position1, position2)
                        positions.forEachIndexed { index, position ->
                            val height = subviewLatitudes[index].height
                            val placement = if (height == 0) {
                                0f
                            } else {
                                activeAnchor.placement * fullLatitude.height.toFloat() / height.toFloat()
                            }
                            views[index].setAnchor(Anchor(position, placement))
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

                override val events: Observable<Event> get() = Observable.merge(views.map { it.events })
                override fun setSight(sight: Sight) = views.forEach { it.setSight(sight) }
                override fun setHBound(hbound: HBound) = views.forEach { it.setHBound(hbound) }
                override val latitudes: Observable<Tower.Latitude> = fullLatitudes.distinctUntilChanged()
                override fun setAnchor(anchor: Anchor) {
                    activeAnchor = anchor
                    updateSubviewAnchors()
                }
            }
    }
}


operator fun <Sight : Any, Event : Any> Tower<Sight, Event>.plus(augment: HAugment<Sight, Event>): Tower<Sight, Event> =
    this.plusAugment(augment)

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendFloor(floorTower: Tower<Sight, Event>): Tower<Sight, Event> =
    this + HAugment.Floor(floorTower)
