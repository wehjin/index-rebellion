package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

sealed class GapSight {
    data class Pixels(val count: Int) : GapSight()
}

object GapTower : Tower<GapSight, Nothing> {
    override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<GapSight, Nothing> =
        object : Tower.View<GapSight, Nothing> {

            override fun dequeue() {}

            override fun setHBound(hbound: HBound) = Unit
            override val latitudes: Observable<Latitude>
                get() = heightBehavior.map { Latitude(it) }

            private val heightBehavior = BehaviorSubject.create<Int>()
            override fun setAnchor(anchor: Anchor) = Unit
            override fun setSight(sight: GapSight) = heightBehavior.onNext((sight as GapSight.Pixels).count)
            override val events: Observable<Nothing> = Observable.never()
        }
}
