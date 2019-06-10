package com.rubyhuntersky.vx

import com.rubyhuntersky.vx.dashes.InputEvent
import com.rubyhuntersky.vx.dashes.InputSight
import com.rubyhuntersky.vx.dashes.TextLineSight
import io.reactivex.Observable

interface Dash<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {
        fun addTextLine(id: ViewId): View<TextLineSight, Nothing>
        fun addInput(id: ViewId): View<InputSight, InputEvent>
    }

    interface View<Sight : Any, Event : Any> {
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
        fun setSight(sight: Sight)
        val events: Observable<Event>
    }

    data class Latitude(val height: Int)

    fun <NeverE : Any> neverEvent(): Dash<Sight, NeverE> =
        object : Dash<Sight, NeverE> {
            override fun enview(viewHost: ViewHost, id: ViewId): View<Sight, NeverE> {
                val coreView = this@Dash.enview(viewHost, id)
                return object : View<Sight, NeverE> {
                    override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
                    override val latitudes: Observable<Latitude> get() = coreView.latitudes
                    override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
                    override fun setSight(sight: Sight) = coreView.setSight(sight)
                    override val events: Observable<NeverE> get() = Observable.never()
                }
            }
        }
}