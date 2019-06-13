package com.rubyhuntersky.vx.tower.tools

import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TestTowerViewHost : Tower.ViewHost {
    sealed class Item {

        abstract val id: ViewId
        abstract val bound: HBound?
        abstract val anchor: Anchor?
        val latitudes: BehaviorSubject<Tower.Latitude> = BehaviorSubject.create()

        data class TextWrap(
            override val id: ViewId,
            override var bound: HBound?,
            override var anchor: Anchor?,
            var sight: TextWrapSight?
        ) : Item()
    }

    override fun addTextWrap(id: ViewId): Tower.View<TextWrapSight, Nothing> {

        val item = Item.TextWrap(id, null, null, null)
            .also { item ->
                items.removeIf { it.id == id }
                items.add(item)
            }

        return object : Tower.View<TextWrapSight, Nothing> {
            override val events: Observable<Nothing> = Observable.never()
            override fun setSight(sight: TextWrapSight) {
                item.sight = sight
            }

            override fun setHBound(hbound: HBound) {
                item.bound = hbound
            }

            override val latitudes: Observable<Tower.Latitude>
                get() = item.latitudes

            override fun setAnchor(anchor: Anchor) {
                item.anchor = anchor
            }
        }
    }

    val items = mutableListOf<Item>()

    override fun addInput(id: ViewId): Tower.View<InputSight, InputEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

