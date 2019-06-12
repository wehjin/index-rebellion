package com.rubyhuntersky.vx.coop

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.orbit.BiOrbit
import io.reactivex.Observable

interface Coop<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {

        fun addSingleTextLineView(textStyle: TextStyle, orbit: BiOrbit, id: ViewId): View<String, Nothing>
    }

    interface View<Sight : Any, Event : Any> {

        val events: Observable<Event>
        fun setBound(bound: BiBound)
        fun setSight(sight: Sight)
    }
}
