package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

fun <CoreSight : Any, Event : Any, EdgeSight : Any> Tower<CoreSight, Event>.mapSight(edgeToCoreSight: (EdgeSight) -> CoreSight): Tower<EdgeSight, Event> {
    val core = this
    return object : Tower<EdgeSight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<EdgeSight, Event> {
            val coreView = core.enview(viewHost, id)
            return object : Tower.View<EdgeSight, Event> {

                override fun dequeue() = coreView.dequeue()

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: EdgeSight) = coreView.setSight(edgeToCoreSight(sight))
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Latitude> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}

fun <Sight : Any, Event : Any> Tower<Sight, Event>.fixSight(fixed: Sight): Tower<Unit, Event> {
    val core = this
    return object : Tower<Unit, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Unit, Event> {
            val coreView = core.enview(viewHost, id)
            return object : Tower.View<Unit, Event> {

                override fun dequeue() = coreView.dequeue()

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Unit) = coreView.setSight(fixed)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Latitude> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}