package com.rubyhuntersky.vx.tower

import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.towers.NeverTower
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable


interface Tower<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, viewId: ViewId): View<Sight, Event>

    interface ViewHost {
        fun drop(viewId: ViewId, start: Boolean)

        fun <Topic : Any> addTextInputView(id: ViewId): View<TextInputSight<Topic>, TextInputEvent<Topic>>

        fun <InnerSight : Any, Topic : Any> addClickOverlayView(
            id: ViewId,
            tower: Tower<InnerSight, Nothing>,
            sightToTopic: (InnerSight) -> Topic
        ): View<InnerSight, ClickEvent<Topic>>

        fun <Topic : Any> addButtonView(id: ViewId): View<ButtonSight<Topic>, ClickEvent<Topic>>

        fun addWrapTextView(id: ViewId): View<WrapTextSight, Nothing>
    }

    interface View<in Sight : Any, Event : Any> : Vx<Sight, Event> {
        fun drop()
        override val events: Observable<Event>
        override fun setSight(sight: Sight)
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Height>
        fun setAnchor(anchor: Anchor)
    }

    fun <NeverE : Any> neverEvent(): Tower<Sight, NeverE> = NeverTower(this)

    infix fun and(tower: Tower<Sight, Event>): Tower<Sight, Event> = extendFloor(tower)
    infix fun shl(share: Share<Sight, Event>): Tower<Sight, Event> {
        return plusHShare(HShare.End(share.span, share.tower))
    }

    infix fun pad(size: Int): Tower<Sight, Event> = this hpad size vpad size
    infix fun hpad(size: Int): Tower<Sight, Event> {
        return plusHMargin(Margin.Uniform(Span.Absolute(size)))
    }

    infix fun vpad(size: Int): Tower<Sight, Event> = plusVPad(VPad.Uniform(size))

    operator fun plus(margin: Margin): Tower<Sight, Event> = plusHMargin(margin)
}

fun <EdgeVision : Any, CoreVision : Any, Event : Any> towerOf(
    coreVisionFromEdge: (EdgeVision) -> CoreVision,
    tower: Tower<CoreVision, Event>
): Tower<EdgeVision, Event> = tower.mapSight(coreVisionFromEdge)
