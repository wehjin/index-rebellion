package com.rubyhuntersky.vx.android.tower

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintSet
import android.view.View
import com.rubyhuntersky.vx.android.toPixels
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class AndroidTowerView<V, in Sight : Any, Event : Any>(
    viewId: ViewId,
    private val hostLayout: ConstraintLayout,
    private val adapter: Adapter<V, Sight, Event>,
    private val onRecycledView: ((View) -> Unit)? = null
) : Tower.View<Sight, Event> where V : View, V : AndroidTowerView.BackingView<Event> {

    interface BackingView<E : Any> {
        var onAttached: (() -> Unit)?
        var onDetached: (() -> Unit)?
        val heights: Observable<Int>
        val events: Observable<E>
    }

    interface Adapter<V, in Sight : Any, Event : Any> where V : View, V : BackingView<Event> {

        fun buildView(context: Context, viewId: ViewId): V
        fun renderView(view: V, sight: Sight)

        fun tagFromId(viewId: ViewId): Any = viewId
        fun findView(hostLayout: ConstraintLayout, viewId: ViewId): Pair<V, Boolean> {
            val old = hostLayout.findViewWithTag<V>(tagFromId(viewId))
            val out = old ?: buildView(hostLayout.context, viewId).apply { tag = tagFromId(viewId) }
            return Pair(out, out != old)
        }
    }

    private val androidViewId = viewId.hashCode()
    private val anchorBehavior = BehaviorSubject.create<Anchor>()
    private val updates = CompositeDisposable()

    private val view = hostLayout.findViewWithTag<V>(adapter.tagFromId(viewId))
        ?.also {
            val isAttached = it.isAttachedToWindow
            if (isAttached) {
                it.onDetached?.invoke()
            }
            it.setUpdateHooks()
            if (isAttached) {
                it.onAttached?.invoke()
            }
            onRecycledView?.invoke(it)
        }
        ?: adapter.buildView(hostLayout.context, viewId).also {
            it.tag = adapter.tagFromId(viewId)
            it.id = androidViewId
            it.visibility = View.INVISIBLE
            it.setUpdateHooks()
            hostLayout.addView(it, 0, LayoutParams.WRAP_CONTENT)
        }

    private fun V.setUpdateHooks() {
        onAttached = {
            Observable.combineLatest(anchorBehavior, heights, ANCHOR_AND_HEIGHT_TO_CEILING)
                .subscribe(this@AndroidTowerView::setCeiling)
                .addTo(updates)
        }
        onDetached = { updates.clear() }
    }

    override fun drop() = updates.clear()

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

    override val latitudes: Observable<Height> get() = view.heights.map { Height(it) }
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