package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

fun <Sight : Any, Event : Any> Tower<Sight, Event>.logAnchors(tag: String): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> {
            val coreView = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, Event> {

                override fun drop() = coreView.drop()

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Height> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) {
                    println("$tag/$viewId: ANCHOR: $anchor")
                    coreView.setAnchor(anchor)
                }
            }
        }
    }
}

fun <Sight : Any, Event : Any> Tower<Sight, Event>.logSight(tag: String): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> {
            val coreView = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, Event> {

                override fun drop() = coreView.drop()

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) {
                    println("$tag/$viewId: SIGHT: $sight")
                    coreView.setSight(sight)
                }

                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Height> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}

fun <Sight : Any, Event : Any> Tower<Sight, Event>.logLatitudes(tag: String): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> {
            val coreView = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, Event> {

                override fun drop() = coreView.drop()

                override val events: Observable<Event> get() = coreView.events
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Height>
                    get() = coreView.latitudes.doOnNext { println("$tag/$viewId: LATITUDE: $it") }

                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}


fun <Sight : Any, Event : Any> Tower<Sight, Event>.logEvents(tag: String): Tower<Sight, Event> {
    val core = this
    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Event> {
            val view = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, Event> {

                override fun drop() = view.drop()

                override val events: Observable<Event>
                    get() {
                        println("$tag/$viewId: EVENTS")
                        return view.events.doOnNext { println("$tag/$viewId: EVENT: $it") }
                    }

                override fun setSight(sight: Sight) = view.setSight(sight)
                override fun setHBound(hbound: HBound) = view.setHBound(hbound)
                override val latitudes: Observable<Height> get() = view.latitudes
                override fun setAnchor(anchor: Anchor) = view.setAnchor(anchor)
            }
        }
    }
}
