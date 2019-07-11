package com.rubyhuntersky.vx.tower

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.tower.additions.augment.VAugment
import com.rubyhuntersky.vx.tower.additions.augment.plusAugment
import com.rubyhuntersky.vx.tower.additions.plusHMargin
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.NeverTower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable


interface Tower<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {
        fun drop(id: ViewId)

        fun addWrapTextView(id: ViewId): View<WrapTextSight, Nothing>

        fun addInputView(id: ViewId): View<InputSight, InputEvent>

        fun <Topic : Any> addClickView(id: ViewId): View<ClickSight<Topic>, ClickEvent<Topic>>

        fun <Sight : Any, Topic : Any> addClickOverlayView(
            id: ViewId,
            tower: Tower<Sight, Nothing>,
            sightToTopic: (Sight) -> Topic
        ): View<Sight, ClickEvent<Topic>>
    }

    interface View<in Sight : Any, Event : Any> {
        val events: Observable<Event>
        fun setSight(sight: Sight)
        fun setHBound(hbound: HBound)
        val latitudes: Observable<Latitude>
        fun setAnchor(anchor: Anchor)
    }

    fun <NeverE : Any> neverEvent(): Tower<Sight, NeverE> = NeverTower(this)

    operator fun plus(margin: Margin): Tower<Sight, Event> = plusHMargin(margin)
    operator fun plus(augment: VAugment<Sight, Event>) = plusAugment(augment)
}
