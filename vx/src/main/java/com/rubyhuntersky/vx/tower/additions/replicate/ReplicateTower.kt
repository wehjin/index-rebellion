package com.rubyhuntersky.vx.tower.additions.replicate

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.Ranked
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
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

    override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<List<Sight>, Ranked<Event>> {

        return object : Tower.View<List<Sight>, Ranked<Event>> {

            private val eventPublish: PublishSubject<Ranked<Event>> = PublishSubject.create()
            private val latitudeBehavior: BehaviorSubject<Latitude> = BehaviorSubject.createDefault(Latitude(0))
            private val eventLatitudeUpdates = CompositeDisposable()
            private var fullView: Tower.View<List<Sight>, Event>? = null
            private var edgeHBound: HBound? = null
            private var edgeAnchor: Anchor? = null

            override fun setSight(sight: List<Sight>) {
                dropFullView()
                val fullTower: Tower<List<Sight>, Event> = (0 until sight.size)
                    .map { index -> itemTower.mapSight { list: List<Sight> -> list[index] } }
                    .fold(EmptyTower(0), Tower<List<Sight>, Event>::extendFloor)
                fullView = fullTower.enview(viewHost, id)
                    .also { view ->
                        view.events
                            .subscribe { event ->
                                eventPublish.onNext(Ranked(event, -1))
                            }
                            .addTo(eventLatitudeUpdates)
                        view.latitudes
                            .subscribe(latitudeBehavior::onNext)
                            .addTo(eventLatitudeUpdates)
                        view.setSight(sight)
                    }
                update()
            }

            private fun dropFullView() {
                eventLatitudeUpdates.clear()
                viewHost.drop(id)
                fullView = null
            }

            private fun update() {
                val view = fullView
                val hBound = edgeHBound
                if (hBound != null && view != null) {
                    view.setHBound(hBound)
                }
                val anchor = edgeAnchor
                if (anchor != null && view != null) {
                    view.setAnchor(anchor)
                }
            }

            override val events: Observable<Ranked<Event>>
                get() = eventPublish

            override fun setHBound(hbound: HBound) {
                edgeHBound = hbound
                update()
            }

            override val latitudes: Observable<Latitude>
                get() = latitudeBehavior.distinctUntilChanged()

            override fun setAnchor(anchor: Anchor) {
                edgeAnchor = anchor
                update()
            }
        }
    }
}