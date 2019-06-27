package com.rubyhuntersky.vx.coop.tools

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TestCoopViewHost : Coop.ViewHost {

    sealed class Item<Sight : Any, Event : Any>(
        val sendEvent: BehaviorSubject<Event> = BehaviorSubject.create(),
        var maybeBound: BiBound? = null,
        var maybeSight: Sight? = null
    ) {
        abstract val id: ViewId

        data class FITTEXT(
            override val id: ViewId,
            val textStyle: TextStyle,
            var orbit: BiOrbit
        ) : Item<String, Nothing>()

        data class TOWER<Sight : Any, Event : Any>(
            override val id: ViewId,
            val tower: Tower<Sight, Event>,
            val eventsBehavior: BehaviorSubject<Event>
        ) : Item<Sight, Event>(eventsBehavior)

        fun asView(): Coop.View<Sight, Event> = object : Coop.View<Sight, Event> {
            override val events: Observable<Event> = this@Item.sendEvent

            override fun setSight(sight: Sight) {
                maybeSight = sight
            }

            override fun setBound(bound: BiBound) {
                maybeBound = bound
            }
        }
    }

    override fun addFitTextView(textStyle: TextStyle, orbit: BiOrbit, id: ViewId): Coop.View<String, Nothing> =
        Item.FITTEXT(id, textStyle, orbit).also(this::addItem).asView()

    private fun addItem(item: Item<*, *>) {
        items.removeIf { it.id == item.id }
        items.add(item)
    }

    val items = mutableListOf<Item<*, *>>()

    override fun <Sight : Any, Event : Any> addTowerView(
        tower: Tower<Sight, Event>,
        id: ViewId
    ): Coop.View<Sight, Event> = Item.TOWER(id, tower, BehaviorSubject.create()).also(this::addItem).asView()

    override fun drop(id: ViewId) {
        items.removeAll { it.id.isDescendentOf(id) }
    }
}

