package com.rubyhuntersky.vx.tower

import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.tower.additions.HShare
import com.rubyhuntersky.vx.tower.additions.Share
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.plusHMargin
import com.rubyhuntersky.vx.tower.additions.plusHShare
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.NeverTower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable


interface Tower<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {
        fun drop(viewId: ViewId, start: Boolean)

        fun <Topic : Any> addTextInputView(id: ViewId): View<TextInputSight<Topic>, TextInputEvent<Topic>>

        fun <InnerSight : Any, Topic : Any> addClickOverlayView(
            id: ViewId,
            tower: Tower<InnerSight, Nothing>,
            sightToTopic: (InnerSight) -> Topic
        ): View<InnerSight, ClickEvent<Topic>>

        fun <Topic : Any> addClickView(id: ViewId): View<ClickSight<Topic>, ClickEvent<Topic>>

        fun addInputView(id: ViewId): View<InputSight, InputEvent>

        fun addWrapTextView(id: ViewId): View<WrapTextSight, Nothing>
    }

    interface View<in Sight : Any, Event : Any> : Vx<Sight, Event> {
        fun dequeue()
        override val events: Observable<Event>
        override fun setSight(sight: Sight)
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
    }

    fun <NeverE : Any> neverEvent(): Tower<Sight, NeverE> = NeverTower(this)

    infix fun and(tower: Tower<Sight, Event>): Tower<Sight, Event> = extendFloor(tower)
    infix fun shl(share: Share<Sight, Event>): Tower<Sight, Event> = plusHShare(HShare.End(share.span, share.tower))
    infix fun pad(size: Int): Tower<Sight, Event> = this hpad size vpad size
    infix fun hpad(size: Int): Tower<Sight, Event> = plusHMargin(Margin.Uniform(Span.Absolute(size)))
    infix fun vpad(size: Int): Tower<Sight, Event> = plusVPad(VPad.Uniform(size))

    operator fun plus(margin: Margin): Tower<Sight, Event> = plusHMargin(margin)
}
