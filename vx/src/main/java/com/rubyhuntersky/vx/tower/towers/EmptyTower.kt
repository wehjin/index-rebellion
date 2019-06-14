package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import kotlin.random.Random

class EmptyTower<Sight : Any, Event : Any> : Tower<Sight, Event> {

    private val towerView = object : Tower.View<Sight, Event> {
        override val events: Observable<Event> get() = Observable.never()
        override fun setSight(sight: Sight) = Unit
        override fun setHBound(hbound: HBound) = Unit
        override val latitudes: Observable<Tower.Latitude> get() = Observable.just(Tower.Latitude(0))
        override fun setAnchor(anchor: Anchor) = Unit
    }

    override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> = towerView

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