package com.rubyhuntersky.vx

import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.dash.Dash
import io.reactivex.Observable

fun <CoreC : Any, EdgeC : Any, Ev : Any> Dash<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): Dash<EdgeC, Ev> {
    return object : Dash<EdgeC, Ev> {
        override fun enview(viewHost: Dash.ViewHost, id: ViewId): Dash.View<EdgeC, Ev> =
            this@transform.enview(viewHost, id).transform(transformer)
    }
}

fun <CoreC : Any, EdgeC : Any, Ev : Any> Dash.View<CoreC, Ev>.transform(transformer: (EdgeC) -> CoreC): Dash.View<EdgeC, Ev> {
    return object : Dash.View<EdgeC, Ev> {
        override fun setHBound(hbound: HBound) = this@transform.setHBound(hbound)
        override val latitudes: Observable<Dash.Latitude> get() = this@transform.latitudes
        override fun setAnchor(anchor: Anchor) = this@transform.setAnchor(anchor)
        override fun setSight(sight: EdgeC) = this@transform.setSight(transformer(sight))
        override val events: Observable<Ev> get() = this@transform.events.map { it }
    }
}

