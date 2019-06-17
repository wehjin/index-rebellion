package com.rubyhuntersky.vx.tower

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import io.reactivex.Observable


interface Tower<Sight : Any, Event : Any> {


    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {
        fun addTextWrapView(id: ViewId): View<WrapTextSight, Nothing>
        fun addInputView(id: ViewId): View<InputSight, InputEvent>
    }

    interface View<in Sight : Any, Event : Any> {
        val events: Observable<Event>
        fun setSight(sight: Sight)
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
    }


    fun <NeverE : Any> neverEvent(): Tower<Sight, NeverE> =
        object : Tower<Sight, NeverE> {
            override fun enview(viewHost: ViewHost, id: ViewId): View<Sight, NeverE> {
                val coreView = this.enview(viewHost, id)
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
