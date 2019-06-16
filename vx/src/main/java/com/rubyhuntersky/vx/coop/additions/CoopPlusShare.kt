package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable
import io.reactivex.Observable.merge


operator fun <Sight : Any, Event : Any> Coop<Sight, Event>.plus(share: Share<Sight, Event>): Coop<Sight, Event> {

    val host = this
    return object : Coop<Sight, Event> {

        override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<Sight, Event> {

            val hostId = id.extend(0)
            val guestId = id.extend(1)
            val hostView = host.enview(viewHost, hostId)
            val guestView = share.coop.enview(viewHost, guestId)

            return object : Coop.View<Sight, Event> {
                override val events: Observable<Event>
                    get() = merge(hostView.events, guestView.events)

                override fun setSight(sight: Sight) {
                    hostView.setSight(sight)
                    guestView.setSight(sight)
                }

                override fun setBound(bound: BiBound) {
                    val (hostBound, guestBound) = when (share.type) {
                        ShareType.HEnd -> bound.splitHEnd(share.span)
                        ShareType.HStart -> bound.splitHStart(share.span).let { Pair(it.second, it.first) }
                        ShareType.VFloor -> bound.splitVFloor(share.span)
                        ShareType.VCeiling -> bound.splitVCeiling(share.span).let { Pair(it.second, it.first) }
                    }
                    hostView.setBound(hostBound)
                    guestView.setBound(guestBound)
                }
            }
        }
    }
}

