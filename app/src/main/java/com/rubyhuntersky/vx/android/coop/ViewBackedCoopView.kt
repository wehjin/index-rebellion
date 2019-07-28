package com.rubyhuntersky.vx.android.coop

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rubyhuntersky.vx.android.toPixels
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable

class ViewBackedCoopView<V, Sight : Any, Event : Any>(
    viewId: ViewId,
    private val frameLayout: FrameLayout,
    private val adapter: Adapter<V, Sight, Event>
) : Coop.View<Sight, Event> where V : View, V : ViewBackedCoopView.BackingView<Event> {

    interface BackingView<E : Any> {
        val events: Observable<E>
    }

    interface Adapter<V, C : Any, E : Any> where V : View, V : BackingView<E> {
        fun buildView(context: Context): V
        fun renderView(view: V, sight: C)
    }

    private val view = (frameLayout.findViewWithTag(viewId)
        ?: adapter.buildView(frameLayout.context)
            .also {
                it.tag = viewId
                frameLayout.addView(
                    it,
                    FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                )
            })

    override fun setBound(bound: BiBound) {
        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
            .apply {
                gravity = Gravity.BOTTOM or Gravity.END
                marginStart = view.toPixels(bound.start).toInt()
                width = view.toPixels(bound.width).toInt()
                marginEnd = frameLayout.width - view.toPixels(bound.end).toInt()
                topMargin = view.toPixels(bound.ceiling).toInt()
                height = view.toPixels(bound.height).toInt()
                bottomMargin = frameLayout.height - view.toPixels(bound.floor).toInt()
            }
    }

    override fun setSight(sight: Sight) {
        Log.d(view.tag.toString(), "Set content $sight")
        adapter.renderView(view, sight)
    }

    override val events: Observable<Event> get() = view.events

    companion object {
        fun isViewInGroup(view: View, groupId: ViewId): Boolean {
            return (view.tag as? ViewId)?.isEqualOrExtends(groupId) ?: false
        }
    }
}