package com.rubyhuntersky.vx.tower.tools

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight
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
        val latitudes: BehaviorSubject<Height> = BehaviorSubject.create()

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
            override var sight: ButtonSight<Topic>? = null,
            val events: PublishSubject<ClickEvent<Topic>> = PublishSubject.create()
        ) : Item()

        data class TestWrapText(
            override val id: ViewId,
            override var bound: HBound? = null,
            override var anchor: Anchor? = null,
            override var sight: WrapTextSight? = null
        ) : Item()
    }

    override fun drop(viewId: ViewId, start: Boolean) {
        if (start) {
            dropViewIds = items.map(Item::id).filter { it.isEqualOrExtends(viewId) }.toSet()
            recycledViewIds.clear()
        } else {
            val subtract = dropViewIds.subtract(recycledViewIds)
            items.removeAll { subtract.contains(it.id) }
            dropViewIds = emptySet()
            recycledViewIds.clear()
        }
    }

    private var dropViewIds: Set<ViewId> = emptySet()
    private val recycledViewIds = mutableSetOf<ViewId>()

    val items = mutableListOf<Item>()

    override fun <Topic : Any> addTextInputView(id: ViewId): Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
        val item = Item.TestEditText<Topic>(id).also(this::addItem)
        return object : Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {

            override fun drop() {}

            override val events get() = item.events
            override fun setSight(sight: TextInputSight<Topic>) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Height> = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    private fun addItem(item: Item) {
        println("ADD ITEM: $item")
        items.removeIf { it.id == item.id }
        items.add(item)
    }

    private fun findItem(id: ViewId): Item? = items.firstOrNull { it.id == id }

    override fun <Sight : Any, Topic : Any> addClickOverlayView(
        id: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ): Tower.View<Sight, ClickEvent<Topic>> {
        val item = Item.TestClickOverlay<Sight, Topic>(id, tower = tower)
        return object : Tower.View<Sight, ClickEvent<Topic>> {

            override fun drop() {}

            override val events: Observable<ClickEvent<Topic>> get() = item.events
            override fun setSight(sight: Sight) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Height> get() = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    override fun <Topic : Any> addButtonView(id: ViewId): Tower.View<ButtonSight<Topic>, ClickEvent<Topic>> {
        val item = Item.TestClick<Topic>(id).also(this::addItem)
        return object : Tower.View<ButtonSight<Topic>, ClickEvent<Topic>> {
            override fun drop() {

            }

            override val events: Observable<ClickEvent<Topic>> get() = item.events
            override fun setSight(sight: ButtonSight<Topic>) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Height> get() = item.latitudes
            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    override fun addWrapTextView(id: ViewId): Tower.View<WrapTextSight, Nothing> {
        val old = findItem(id) as? Item.TestWrapText
        if (old == null) {
            val item = Item.TestWrapText(id).also(this::addItem)
            return object : Tower.View<WrapTextSight, Nothing> {

                override fun drop() {}

                override val events: Observable<Nothing> get() = Observable.never()
                override fun setSight(sight: WrapTextSight) {
                    item.sight = sight
                }

                override fun setHBound(hbound: HBound) {
                    item.bound = hbound
                }

                override val latitudes: Observable<Height> get() = item.latitudes
                override fun setAnchor(anchor: Anchor) {
                    item.anchor = anchor
                }
            }
        } else {
            recycledViewIds.add(id)
            return object : Tower.View<WrapTextSight, Nothing> {

                override fun drop() {}

                override val events: Observable<Nothing> get() = Observable.never()
                override fun setSight(sight: WrapTextSight) {
                    old.sight = sight
                }

                override fun setHBound(hbound: HBound) {
                    old.bound = hbound
                }

                override val latitudes: Observable<Height> get() = old.latitudes
                override fun setAnchor(anchor: Anchor) {
                    old.anchor = anchor
                }
            }
        }
    }
}

