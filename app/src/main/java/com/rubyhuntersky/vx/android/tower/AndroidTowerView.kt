package com.rubyhuntersky.vx.android.tower

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintSet
import android.view.View
import com.rubyhuntersky.vx.android.toPixels
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class AndroidTowerView<V, Sight : Any, Event : Any>(
    viewId: ViewId,
    private val hostLayout: ConstraintLayout,
    private val adapter: Adapter<V, Sight, Event>
) : Tower.View<Sight, Event> where V : View, V : AndroidTowerView.BackingView<Event> {

    interface BackingView<E : Any> {
        var onAttached: (() -> Unit)?
        var onDetached: (() -> Unit)?
        val heights: Observable<Int>
        val events: Observable<E>
    }

    interface Adapter<V, Sight : Any, Event : Any> where V : View, V : BackingView<Event> {
        fun buildView(context: Context): V
        fun renderView(view: V, sight: Sight)
    }

    private val updates = CompositeDisposable()
    private val anchorBehavior = BehaviorSubject.create<Anchor>()

    private val androidViewId = viewId.hashCode()
    private val view = hostLayout.findViewWithTag(viewId) ?: adapter.buildView(hostLayout.context)
        .apply {
            id = androidViewId
            tag = viewId
            visibility = View.INVISIBLE
            onAttached = {
                Observable.combineLatest(anchorBehavior, heights, ANCHOR_AND_HEIGHT_TO_CEILING)
                    .subscribe(this@AndroidTowerView::setCeiling)
                    .addTo(updates)
            }
            onDetached = { updates.clear() }
        }.also {
            hostLayout.addView(it, 0, LayoutParams.WRAP_CONTENT)
        }

    private var activeCeiling: Int? = null
    private fun setCeiling(ceiling: Int) {
        if (ceiling != activeCeiling) {
            activeCeiling = ceiling
            val topMargin = hostLayout.toPixels(ceiling).toInt()
            ConstraintSet()
                .apply {
                    clone(hostLayout)
                    connect(androidViewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin)
                }
                .applyTo(hostLayout)
            view.visibility = View.VISIBLE
        }
    }

    override fun setHBound(hbound: HBound) {
        val startMargin = hostLayout.toPixels(hbound.start).toInt()
        val endMargin = hostLayout.width - hostLayout.toPixels(hbound.end).toInt()
        ConstraintSet()
            .apply {
                clone(hostLayout)
                connect(androidViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin)
                connect(androidViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, endMargin)
            }
            .applyTo(hostLayout)
    }

    override val latitudes: Observable<Latitude> get() = view.heights.map { Latitude(it) }
    override fun setAnchor(anchor: Anchor) = anchorBehavior.onNext(anchor)

    override val events: Observable<Event> get() = view.events
    override fun setSight(sight: Sight) = adapter.renderView(view, sight)

    companion object {
        fun isViewInGroup(view: View, groupId: ViewId): Boolean {
            val viewId = view.tag as? ViewId
            return viewId?.isEqualOrExtends(groupId) ?: false
        }

        @Suppress("unused")
        private val TAG = AndroidTowerView::class.java.simpleName
        private val ANCHOR_AND_HEIGHT_TO_CEILING = BiFunction(Anchor::toCeiling)
    }
}