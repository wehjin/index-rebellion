package com.rubyhuntersky.vx

import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

fun <CoreC : Any, EdgeC : Any, Ev : Any> Tower<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): Tower<EdgeC, Ev> {
    return object : Tower<EdgeC, Ev> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<EdgeC, Ev> =
            this@transform.enview(viewHost, id).transform(transformer)
    }
}

fun <CoreC : Any, EdgeC : Any, Ev : Any> Tower.View<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): Tower.View<EdgeC, Ev> {
    return object : Tower.View<EdgeC, Ev> {
        override fun setHBound(hbound: HBound) = this@transform.setHBound(hbound)
        override val latitudes: Observable<Tower.Latitude> get() = this@transform.latitudes
        override fun setAnchor(anchor: Anchor) = this@transform.setAnchor(anchor)
        override fun setSight(sight: EdgeC) = this@transform.setSight(transformer(sight))
        override val events: Observable<Ev> get() = this@transform.events.map { it }
    }
}

