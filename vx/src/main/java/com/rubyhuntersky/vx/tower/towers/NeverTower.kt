package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable

class NeverTower<Sight : Any, NeverE : Any, EverE : Any>(
    private val tower: Tower<Sight, EverE>
) : Tower<Sight, NeverE> {

    private class NeverView<in Sight : Any, NeverE : Any, EverE : Any>(
        private val coreView: Tower.View<Sight, EverE>
    ) : Tower.View<Sight, NeverE> {

        override fun drop() = coreView.drop()
        override fun setHBound(hbound: HBound) = coreView.setHBound(hbound)
        override val latitudes: Observable<Height> get() = coreView.latitudes
        override fun setAnchor(anchor: Anchor) = coreView.setAnchor(anchor)
        override fun setSight(sight: Sight) = coreView.setSight(sight)
        override val events: Observable<NeverE> get() = Observable.never()
    }

    override fun enview(viewHost: Tower.ViewHost, viewId: ViewId): Tower.View<Sight, NeverE> {
        return NeverView(tower.enview(viewHost, viewId))
    }
}

