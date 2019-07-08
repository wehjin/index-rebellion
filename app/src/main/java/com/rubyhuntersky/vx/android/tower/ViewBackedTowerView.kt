package com.rubyhuntersky.vx.android.tower

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.rubyhuntersky.vx.android.toPixels
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.bound.VBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.toSizeAnchor
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class ViewBackedTowerView<V, Sight : Any, Event : Any>(
    id: ViewId,
    private val frameLayout: FrameLayout,
    private val adapter: Adapter<V, Sight, Event>
) : Tower.View<Sight, Event> where V : View, V : ViewBackedTowerView.BackingView<Event> {

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

    private val view = (frameLayout.findViewWithTag(id)
        ?: adapter.buildView(frameLayout.context)
            .also {
                it.tag = id
                it.onAttached = {
                    Observable.combineLatest(it.heights, anchorBehavior, toSizeAnchor)
                        .distinctUntilChanged()
                        .subscribe { sizeAnchor ->
                            Log.v(it.tag.toString(), "onSizeAnchor $sizeAnchor")
                            setVBound(sizeAnchor.vBound)
                        }
                        .addTo(updates)
                }
                it.onDetached = { updates.clear() }
                frameLayout.addView(
                    it,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            })

    private fun setVBound(vbound: VBound) {
        Log.v("${view.javaClass.simpleName}/${view.tag}", "Set vbound $vbound")
        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
            .apply {
                topMargin = view.toPixels(vbound.ceiling).toInt()
            }
    }

    override fun setHBound(hbound: HBound) {
        Log.v(view.tag.toString(), "Set hbound $hbound")
        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
            .apply {
                gravity = Gravity.END
                marginStart = view.toPixels(hbound.start).toInt()
                marginEnd = frameLayout.width - (marginStart + view.toPixels(hbound.width).toInt())
            }
    }

    override val latitudes: Observable<Latitude> get() = view.heights.map { Latitude(it) }
    override fun setAnchor(anchor: Anchor) = anchorBehavior.onNext(anchor)

    override val events: Observable<Event> get() = view.events
    override fun setSight(sight: Sight) = adapter.renderView(view, sight)

    companion object {
        fun isViewInGroup(view: View, groupId: ViewId): Boolean {
            return (view.tag as? ViewId)?.isEqualOrExtends(groupId) ?: false
        }
    }
}