package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable


operator fun <CoreSight : Any, Event : Any, EdgeSight : Any> Coop<CoreSight, Event>.div(edgeToCoreSight: (EdgeSight) -> CoreSight): Coop<EdgeSight, Event> {
    val core = this
    return object : Coop<EdgeSight, Event> {
        override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<EdgeSight, Event> {
            val coreView = core.enview(viewHost, id)
            return object : Coop.View<EdgeSight, Event> {
                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: EdgeSight) = coreView.setSight(edgeToCoreSight(sight))
                override fun setBound(bound: BiBound) = coreView.setBound(bound)
            }
        }
    }
}

