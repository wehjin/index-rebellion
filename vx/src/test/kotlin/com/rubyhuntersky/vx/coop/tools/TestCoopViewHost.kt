package com.rubyhuntersky.vx.coop.tools

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bounds.BiBound
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable

class TestCoopViewHost : Coop.ViewHost {

    sealed class Item {

        abstract val id: ViewId
        abstract val bound: BiBound?

        data class SingleTextLine(
            override val id: ViewId,
            override var bound: BiBound?,
            var sight: String?,
            val textStyle: TextStyle
        ) : Item()
    }


    val items = mutableListOf<Item>()

    override fun addSingleTextLineView(textStyle: TextStyle, id: ViewId): Coop.View<String, Nothing> {

        val item = Item.SingleTextLine(id, null, null, textStyle)
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

