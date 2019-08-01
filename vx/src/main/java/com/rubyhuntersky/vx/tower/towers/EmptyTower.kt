package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import kotlin.random.Random

class EmptyTower<Sight : Any, Event : Any>(private val height: Int = 0) : Tower<Sight, Event> {

    override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> =
        object : Tower.View<Sight, Event> {

            override fun dequeue() {}

            override fun setSight(sight: Sight) = Unit
            override val events: Observable<Event> get() = Observable.never()
            override fun setHBound(hbound: HBound) = Unit
            override val latitudes: Observable<Latitude> get() = Observable.just(Latitude(height))
            override fun setAnchor(anchor: Anchor) = Unit
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmptyTower<*, *>) return false
        return hashCode() == other.hashCode()
    }

    override fun hashCode(): Int = code

    companion object {
        private val code = Random.nextInt()
    }
}