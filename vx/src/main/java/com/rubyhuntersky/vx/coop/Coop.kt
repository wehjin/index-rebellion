package com.rubyhuntersky.vx.coop

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bounds.BiBound
import io.reactivex.Observable

interface Coop<Sight : Any, Event : Any> {

    fun enview(viewHost: ViewHost, id: ViewId): View<Sight, Event>

    interface ViewHost {

        fun addSingleTextLineView(textStyle: TextStyle, id: ViewId): View<String, Nothing>
    }

    interface View<Sight : Any, Event : Any> {

        val events: Observable<Event>
        fun setBound(bound: BiBound)
        fun setSight(sight: Sight)
    }
}
