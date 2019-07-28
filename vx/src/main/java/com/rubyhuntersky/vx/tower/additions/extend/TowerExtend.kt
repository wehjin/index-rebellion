package com.rubyhuntersky.vx.tower.additions.extend

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendFloor(tower: Tower<Sight, Event>) =
    extendVertical(VExtend.Floor(tower))

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendFloors(vararg floors: Tower<Sight, Event>) =
    floors.fold(this, Tower<Sight, Event>::extendFloor)

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendCeiling(tower: Tower<Sight, Event>) =
    extendVertical(VExtend.Ceiling(tower))

fun <Sight : Any, Event : Any> Tower<Sight, Event>.extendVertical(extend: VExtend<Sight, Event>): Tower<Sight, Event> {
    val coreTowers = listOf(extend.ceilingTower, this, extend.floorTower)
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> =
            object : Tower.View<Sight, Event> {
                private val coreViews = coreTowers
                    .mapIndexed { index, tower -> tower.enview(viewHost, id.extend(index)) }

                override fun dequeue() {
                    updates.clear()
                    coreViews.forEach(Tower.View<Sight, Event>::dequeue)
                }

                private val updates = CompositeDisposable()
                private val coreHeights = BehaviorSubject.create<Pair<List<Int>, Int>>()
                private val edgeAnchors = PublishSubject.create<Anchor>()

                init {
                    combineLatest(coreViews.map(Tower.View<Sight, Event>::latitudes)) { coreViewLatitudes ->
                        val coreHeight = coreViewLatitudes
                            .map { (it as Latitude).height }
                            .fold(
                                initial = Pair(emptyList<Int>(), 0),
                                operation = { (minorHeights, majorHeight), coreViewHeight ->
                                    Pair(minorHeights + majorHeight, majorHeight + coreViewHeight)
                                }
                            )
                        coreHeight
                    }.subscribe(coreHeights::onNext).addTo(updates)

                    combineLatest<Pair<List<Int>, Int>, Anchor, Triple<List<Int>, Int, Anchor>>(
                        coreHeights, edgeAnchors,
                        BiFunction { coreHeight, edgeAnchor ->
                            Triple(coreHeight.first, coreHeight.second, edgeAnchor)
                        })
                        .subscribe { (coreOffsets, coreFullHeight, edgeAnchor) ->
                            val edgeVBound = edgeAnchor.toVBound(coreFullHeight)
                            coreViews.forEachIndexed { index, coreView ->
                                coreView.setAnchor(Anchor(edgeVBound.ceiling + coreOffsets[index], 0f))
                            }
                        }
                        .addTo(updates)
                }

                override val events: Observable<Event> get() = Observable.merge(coreViews.map { it.events })
                override fun setSight(sight: Sight) = coreViews.forEach { it.setSight(sight) }
                override fun setHBound(hbound: HBound) = coreViews.forEach { it.setHBound(hbound) }
                override val latitudes: Observable<Latitude> get() = coreHeights.map { Latitude(it.second) }.distinctUntilChanged()
                override fun setAnchor(anchor: Anchor) = edgeAnchors.onNext(anchor)
            }
    }
}
