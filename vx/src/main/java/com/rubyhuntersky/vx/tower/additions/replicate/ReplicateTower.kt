package com.rubyhuntersky.vx.tower.additions.replicate

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.Ranked
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.mapEvent
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ReplicateTower<Sight : Any, Event : Any>(
    private val itemTower: Tower<Sight, Event>
) : Tower<List<Sight>, Ranked<Event>> {

    override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<List<Sight>, Ranked<Event>> {

        return object : Tower.View<List<Sight>, Ranked<Event>> {

            private val eventPublish: PublishSubject<Ranked<Event>> = PublishSubject.create()
            private val heightBehavior: BehaviorSubject<Height> = BehaviorSubject.createDefault(Height(0))
            private val eventLatitudeUpdates = CompositeDisposable()
            private var fullView: Tower.View<List<Sight>, Ranked<Event>>? = null
            private var edgeHBound: HBound? = null
            private var edgeAnchor: Anchor? = null

            override fun setSight(sight: List<Sight>) {
                eventLatitudeUpdates.clear()
                viewHost.drop(viewId, true)
                fullView?.drop()
                fullView = null

                val fullTower: Tower<List<Sight>, Ranked<Event>> =
                    (0 until sight.size)
                        .map {
                            itemTower.mapSight { list: List<Sight> -> list[it] }
                        }
                        .foldIndexed(
                            initial = EmptyTower<List<Sight>, Ranked<Event>>(0),
                            operation = { index, acc: Tower<List<Sight>, Ranked<Event>>, tower ->
                                acc.extendFloor(tower.mapEvent { Ranked(it, index) })
                            })

                fullView = fullTower.enview(viewHost, viewId)
                    .apply {
                        events.subscribe(eventPublish::onNext).addTo(eventLatitudeUpdates)
                        latitudes.subscribe {
                            updateAnchor()
                            heightBehavior.onNext(it)
                        }.addTo(eventLatitudeUpdates)
                        setSight(sight)
                    }
                viewHost.drop(viewId, false)
                update()
            }

            override fun drop() {
                fullView?.drop()
            }

            private fun update() {
                updateHBound()
                updateAnchor()
            }

            private fun updateHBound() {
                val fullView = fullView
                val hBound = edgeHBound
                if (hBound != null && fullView != null) {
                    fullView.setHBound(hBound)
                }
            }

            private fun updateAnchor() {
                val fullView = fullView
                val anchor = edgeAnchor
                if (anchor != null && fullView != null) {
                    fullView.setAnchor(anchor)
                }
            }

            override val events: Observable<Ranked<Event>>
                get() = eventPublish

            override fun setHBound(hbound: HBound) {
                edgeHBound = hbound
                update()
            }

            override val latitudes: Observable<Height>
                get() = heightBehavior.distinctUntilChanged()

            override fun setAnchor(anchor: Anchor) {
                edgeAnchor = anchor
                update()
            }
        }
    }
}