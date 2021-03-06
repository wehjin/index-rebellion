package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

fun <Sight : Any, Event : Any> Tower<Sight, Event>.plusHMargin(margin: Margin): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> {
            val coreView = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, Event> {

                override fun drop() = coreView.drop()

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound.withMargin(margin))
                override val latitudes: Observable<Height> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}
