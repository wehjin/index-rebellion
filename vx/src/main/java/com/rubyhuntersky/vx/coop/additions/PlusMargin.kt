package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.coop.additions.margin.BiMargin
import io.reactivex.Observable

operator fun <Sight : Any, Event : Any> Coop<Sight, Event>.plus(margin: BiMargin): Coop<Sight, Event> {
    val coreCoop = this
    return object : Coop<Sight, Event> {
        override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<Sight, Event> {
            val coreView = coreCoop.enview(viewHost, id)
            return object : Coop.View<Sight, Event> {
                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setBound(bound: BiBound) {
                    val coreBound = bound.withMargin(margin)
                    coreView.setBound(coreBound)
                }
            }
        }
    }
}

