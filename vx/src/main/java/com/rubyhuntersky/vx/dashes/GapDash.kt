package com.rubyhuntersky.vx.dashes

import com.rubyhuntersky.vx.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class GapSight {
    data class Pixels(val count: Int) : GapSight()
}

object GapDash : Dash<GapSight, Nothing> {
    override fun enview(viewHost: ViewHost, id: ViewId): Dash.View<GapSight, Nothing> =
        object : Dash.View<GapSight, Nothing> {
            override fun setHBound(hbound: HBound) = Unit
            override val latitudes: Observable<Dash.Latitude> get() = heightBehavior.map { Dash.Latitude(it) }
            private val heightBehavior = BehaviorSubject.create<Int>()
            override fun setAnchor(anchor: Anchor) = Unit
            override fun setSight(content: GapSight) = heightBehavior.onNext((content as GapSight.Pixels).count)
            override val events: Observable<Nothing> = Observable.never()
        }
}