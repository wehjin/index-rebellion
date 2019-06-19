package com.rubyhuntersky.vx.tower.tools

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TestTowerViewHost : Tower.ViewHost {
    sealed class Item {

        abstract val id: ViewId
        abstract val bound: HBound?
        abstract val anchor: Anchor?
        abstract val sight: Any?
        val latitudes: BehaviorSubject<Latitude> = BehaviorSubject.create()

        data class TestWrapText(
            override val id: ViewId,
            override var bound: HBound?,
            override var anchor: Anchor?,
            override var sight: WrapTextSight?
        ) : Item()
    }

    override fun addTextWrapView(id: ViewId): Tower.View<WrapTextSight, Nothing> {

        val item = Item.TestWrapText(id, null, null, null)
            .also { item ->
                items.removeIf { it.id == id }
                items.add(item)
            }

        return object : Tower.View<WrapTextSight, Nothing> {
            override val events: Observable<Nothing> = Observable.never()
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

    val items = mutableListOf<Item>()

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun drop(id: ViewId) {
        items.removeAll { it.id.isDescendentOf(id) }
    }
}

