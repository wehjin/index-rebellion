package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable

object EmptyCoop : Coop<Unit, Nothing> {

    object EmptyCoopView : Coop.View<Unit, Nothing> {
        override val events: Observable<Nothing> = Observable.never()
        override fun setSight(sight: Unit) = Unit
        override fun setBound(bound: BiBound) = Unit
    }

    override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<Unit, Nothing> =
        EmptyCoopView
}