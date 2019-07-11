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
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
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

        data class TestEditText<Topic : Any>(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: TextInputSight<Topic>? = null,
            val events: PublishSubject<TextInputEvent<Topic>> = PublishSubject.create()
        ) : Item()

        data class TestClickOverlay<Sight : Any, Topic : Any>(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: Sight? = null,
            val tower: Tower<Sight, Nothing>,
            val events: PublishSubject<ClickEvent<Topic>> = PublishSubject.create()
        ) : Item()

        data class TestClick<Topic : Any>(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: ClickSight<Topic>? = null,
            val events: PublishSubject<ClickEvent<Topic>> = PublishSubject.create()
        ) : Item()

        data class TestWrapText(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: WrapTextSight? = null
        ) : Item()
    }

    override fun drop(id: ViewId) {
        items.removeAll { it.id.isEqualOrExtends(id) }
    }

    val items = mutableListOf<Item>()

    override fun <Topic : Any> addTextInputView(id: ViewId): Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
        val item = Item.TestEditText<Topic>(id).also(this::addItem)
        return object : Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
            override val events get() = item.events
            override fun setSight(sight: TextInputSight<Topic>) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Latitude> = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    private fun addItem(item: Item) {
        items.removeIf { it.id == item.id }
        items.add(item)
    }

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <Sight : Any, Topic : Any> addClickOverlayView(
        id: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ): Tower.View<Sight, ClickEvent<Topic>> {
        val item = Item.TestClickOverlay<Sight, Topic>(id, tower = tower)
        return object : Tower.View<Sight, ClickEvent<Topic>> {
            override val events: Observable<ClickEvent<Topic>> get() = item.events
            override fun setSight(sight: Sight) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Latitude> get() = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    override fun <Topic : Any> addClickView(id: ViewId): Tower.View<ClickSight<Topic>, ClickEvent<Topic>> {
        val item = Item.TestClick<Topic>(id).also(this::addItem)
        return object : Tower.View<ClickSight<Topic>, ClickEvent<Topic>> {
            override val events: Observable<ClickEvent<Topic>> get() = item.events
            override fun setSight(sight: ClickSight<Topic>) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Latitude> get() = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    override fun addWrapTextView(id: ViewId): Tower.View<WrapTextSight, Nothing> {
        val item = Item.TestWrapText(id).also(this::addItem)
        return object : Tower.View<WrapTextSight, Nothing> {
            override val events: Observable<Nothing> get() = Observable.never()
            override fun setSight(sight: WrapTextSight) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Latitude> get() = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }
}

