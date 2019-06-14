package com.rubyhuntersky.vx.coop.tools

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import io.reactivex.Observable

class TestCoopViewHost : Coop.ViewHost {

    sealed class Item {

        abstract val id: ViewId
        abstract val bound: BiBound?
        abstract val sight: Any?

        data class SingleTextLine(
            override val id: ViewId,
            override var bound: BiBound?,
            override var sight: String?,
            val textStyle: TextStyle,
            var orbit: BiOrbit
        ) : Item()
    }


    val items = mutableListOf<Item>()

    override fun addSingleTextLineView(textStyle: TextStyle, orbit: BiOrbit, id: ViewId): Coop.View<String, Nothing> {

        val item = Item.SingleTextLine(id, null, null, textStyle, orbit)
            .also { item ->
                items.removeIf { it.id == id }
                items.add(item)
            }
        return object : Coop.View<String, Nothing> {

            override val events: Observable<Nothing> = Observable.never()

            override fun setBound(bound: BiBound) {
                item.bound = bound
            }

            override fun setSight(sight: String) {
                item.sight = sight
            }
        }
    }
}

