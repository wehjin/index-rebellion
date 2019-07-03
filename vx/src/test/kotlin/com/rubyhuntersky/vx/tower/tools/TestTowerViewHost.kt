package com.rubyhuntersky.vx.tower.tools

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class TestTowerViewHost : Tower.ViewHost {
    sealed class Item {

        abstract val id: ViewId
        abstract val bound: HBound?
        abstract val anchor: Anchor?
        abstract val sight: Any?
        val latitudes: BehaviorSubject<Latitude> = BehaviorSubject.create()

        data class TestWrapText(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: WrapTextSight? = null
        ) : Item()

        data class TestClick(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: ClickSight? = null,
            val events: PublishSubject<ClickEvent> = PublishSubject.create()
        ) : Item()
    }

    override fun addWrapTextView(id: ViewId): Tower.View<WrapTextSight, Nothing> {

        val item = Item.TestWrapText(id).also(this::addItem)
        return object : Tower.View<WrapTextSight, Nothing> {
            override val events: Observable<Nothing>
                get() = Observable.never()

            override fun setSight(sight: WrapTextSight) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Latitude>
                get() = item.latitudes

            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    private fun addItem(item: Item) {
        items.removeIf { it.id == item.id }
        items.add(item)
    }

    val items = mutableListOf<Item>()

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addClickView(id: ViewId): Tower.View<ClickSight, ClickEvent> {
        val item = Item.TestClick(id).also(this::addItem)
        return object : Tower.View<ClickSight, ClickEvent> {
            override val events: Observable<ClickEvent>
                get() = item.events

            override fun setSight(sight: ClickSight) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Latitude>
                get() = item.latitudes

            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    override fun drop(id: ViewId) {
        items.removeAll { it.id.isEqualOrExtends(id) }
    }
}

