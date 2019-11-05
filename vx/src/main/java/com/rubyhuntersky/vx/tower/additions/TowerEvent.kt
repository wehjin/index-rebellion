package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

fun <Sight : Any, CoreEvent : Any, EdgeEvent : Any> Tower<Sight, CoreEvent>.mapEvent(coreToEdgeEvent: ((CoreEvent) -> EdgeEvent)): Tower<Sight, EdgeEvent> {
    val core = this
    return object : Tower<Sight, EdgeEvent> {
        override fun enview(
            viewHost: Tower.ViewHost,
            viewId: ViewId
        ): Tower.View<Sight, EdgeEvent> {
            val coreView = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, EdgeEvent> {

                override fun drop() = coreView.drop()

                override val events: Observable<EdgeEvent>
                    get() = coreView.events.map(coreToEdgeEvent)

                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Height> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}

fun <Sight : Any, Event : Any> Tower<Sight, Event>.handleEvents(onEvent: ((Event) -> Unit)): Tower<Sight, Nothing> {
    val core = this
    return object : Tower<Sight, Nothing> {
        override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, Nothing> {
            val coreView = core.enview(viewHost, viewId)
            return object : Tower.View<Sight, Nothing> {

                override fun drop() {
                    eventHandler.dispose()
                    coreView.drop()
                }

                private val eventHandler = coreView.events.subscribe(onEvent)

                override val events: Observable<Nothing> get() = coreView.events.flatMap { Observable.never<Nothing>() }
                override fun setSight(sight: Sight) = coreView.setSight(sight)
                override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                override val latitudes: Observable<Height> get() = coreView.latitudes
                override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
            }
        }
    }
}

