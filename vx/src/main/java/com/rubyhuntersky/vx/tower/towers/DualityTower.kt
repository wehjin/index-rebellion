package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.*
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.Observable.merge
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class DualityTower<YinSight : Any, YangSight : Any, Event : Any>(
    private val yinTower: Tower<YinSight, Event>,
    private val yangTower: Tower<YangSight, Event>
) : Tower<Duality<YinSight, YangSight>, Event> {

    override fun enview(
        viewHost: Tower.ViewHost,
        viewId: ViewId
    ): Tower.View<Duality<YinSight, YangSight>, Event> {

        return object : Tower.View<Duality<YinSight, YangSight>, Event> {

            private var pickYin: Boolean = true
            private val yinView = MortalTower(yinTower).enview(viewHost, viewId.extend(0))
            private val yangView = MortalTower(yangTower).enview(viewHost, viewId.extend(1))
            private val edgeEvents: PublishSubject<Event> = PublishSubject.create()
            private val edgeLatitudes = BehaviorSubject.createDefault(Height(0))
            private var edgeAnchor: Anchor? = null
            private val edgeUpdates = CompositeDisposable()

            init {
                combineLatest<Height, Height, Height>(
                    yinView.latitudes,
                    yangView.latitudes,
                    BiFunction { yin, yang -> if (pickYin) yin else yang })
                    .subscribe {
                        updateCoreAnchors(edgeAnchor)
                        edgeLatitudes.onNext(it)
                    }
                    .addTo(edgeUpdates)

                merge(yinView.events.map { Duality.Yin(it) }, yangView.events.map { Duality.Yang(it) })
                    .filter { if (pickYin) it.isYin else it.isYang }
                    .map {
                        when (it) {
                            is Duality.Yin -> it.y
                            is Duality.Yang -> it.y
                        }
                    }
                    .subscribe(edgeEvents::onNext)
                    .addTo(edgeUpdates)
            }

            override fun drop() {
                yinView.drop()
                yangView.drop()
            }

            override val events: Observable<Event>
                get() = edgeEvents

            override fun setSight(sight: Duality<YinSight, YangSight>) {
                when (sight) {
                    is Duality.Yin -> {
                        pickYin = true
                        yangView.setSight(Mortal.NotToBe)
                        yinView.setSight(sight.y.toMortal())
                    }
                    is Duality.Yang -> {
                        pickYin = false
                        yinView.setSight(Mortal.NotToBe)
                        yangView.setSight(sight.y.toMortal())
                    }
                }
            }

            override fun setHBound(hbound: HBound) {
                yinView.setHBound(hbound)
                yangView.setHBound(hbound)
            }

            override val latitudes: Observable<Height>
                get() = edgeLatitudes.distinctUntilChanged()

            override fun setAnchor(anchor: Anchor) {
                edgeAnchor = anchor
                updateCoreAnchors(anchor)
            }

            private fun updateCoreAnchors(edgeAnchor: Anchor?) {
                edgeAnchor?.let {
                    yinView.setAnchor(it)
                    yangView.setAnchor(it)
                }
            }
        }
    }
}