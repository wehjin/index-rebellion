package com.rubyhuntersky.vx.coop

import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

interface Coop<in Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {
        fun addFitTextView(textStyle: TextStyle, orbit: BiOrbit, id: ViewId): View<String, Nothing>
        fun <Sight : Any, Event : Any> addTowerView(tower: Tower<Sight, Event>, id: ViewId): View<Sight, Event>
        fun drop(id: ViewId)
    }

    interface View<in Sight : Any, Event : Any> : Vx<Sight, Event> {
        override val events: Observable<Event>
        override fun setSight(sight: Sight)
        fun setBound(bound: BiBound)
    }
}

