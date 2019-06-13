package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.margin.Margin
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

operator fun <Sight : Any, Event : Any> Tower<Sight, Event>.plus(margin: Margin): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> {
            val coreView = core.enview(viewHost, id)
            return object : Tower.View<Sight, Event> {
                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override val latitudes: Observable<Tower.Latitude>
                    get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

                override fun setAnchor(anchor: Anchor) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }
        }
    }
}

