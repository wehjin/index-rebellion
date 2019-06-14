package com.rubyhuntersky.vx.coop

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import io.reactivex.Observable

interface Coop<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {
        fun addSingleTextLineView(textStyle: TextStyle, orbit: BiOrbit, id: ViewId): View<String, Nothing>
    }

    interface View<in Sight : Any, Event : Any> {
        val events: Observable<Event>
        fun setSight(sight: Sight)
        fun setBound(bound: BiBound)
    }
}

