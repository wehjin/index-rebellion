package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

sealed class Gap {

    object TitleBody : Gap() {
        const val dips: Int = 24
    }
}

operator fun <Sight : Any, Event : Any> Tower<Sight, Event>.plus(gap: Gap): Tower<Sight, Event> =
    object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> =
            object : Tower.View<Sight, Event> {
                private val view = this@plus.enview(viewHost, id.extend(0))
                private val heights = view.latitudes.map { it.height + (gap as Gap.TitleBody).dips }
                private val anchorBehavior = BehaviorSubject.createDefault(Anchor(0, 0f))
                private val composite = CompositeDisposable()

                init {
                    val sizeAnchors = Observable.combineLatest(heights, anchorBehavior, toSizeAnchor)
                    sizeAnchors.distinctUntilChanged()
                        .subscribe { sizeAnchor ->
                            val topBottom = sizeAnchor.anchor.toBound(sizeAnchor.size)
                            view.setAnchor(Anchor(topBottom.first, 0f))
                        }.addTo(composite)
                }

                override fun setHBound(hbound: HBound) = view.setHBound(hbound)
                override val latitudes: Observable<Latitude>
                    get() = heights.map { Latitude(it) }

                override fun setAnchor(anchor: Anchor) = anchorBehavior.onNext(anchor)
                override fun setSight(sight: Sight) = view.setSight(sight)
                override val events: Observable<Event> get() = view.events
            }
    }
