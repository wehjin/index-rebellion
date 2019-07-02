package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

fun <Sight : Any, CoreEvent : Any, EdgeEvent : Any> Tower<Sight, CoreEvent>.mapEvent(coreToEdgeEvent: ((CoreEvent) -> EdgeEvent)): Tower<Sight, EdgeEvent> {
    val core = this
    return object : Tower<Sight, EdgeEvent> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, EdgeEvent> {
            val coreView = core.enview(viewHost, id)
            return object : Tower.View<Sight, EdgeEvent> {

                override val events: Observable<EdgeEvent>
                    get() = coreView.events.map(coreToEdgeEvent)

                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Latitude> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}
