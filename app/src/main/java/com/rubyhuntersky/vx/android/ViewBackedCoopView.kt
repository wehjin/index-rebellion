package com.rubyhuntersky.vx.android

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable

class ViewBackedCoopView<V, C : Any, E : Any>(
    id: ViewId,
    private val frameLayout: FrameLayout,
    private val adapter: Adapter<V, C, E>
) : Coop.View<C, E> where V : View, V : ViewBackedCoopView.BackingView<E> {

    interface BackingView<E : Any> {
        val events: Observable<E>
    }

    interface Adapter<V, C : Any, E : Any> where V : View, V : BackingView<E> {
        fun buildView(context: Context): V
        fun renderView(view: V, sight: C)
    }

    private val view = (frameLayout.findViewWithTag(id)
        ?: adapter.buildView(frameLayout.context)
            .also {
                it.tag = id
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

    override fun setSight(sight: C) {
        Log.d(view.tag.toString(), "Set content $sight")
        adapter.renderView(view, sight)
    }

    override val events: Observable<E> get() = view.events

    companion object {
        fun isViewInGroup(view: View, groupId: ViewId): Boolean {
            return (view.tag as? ViewId)?.isDescendentOf(groupId) ?: false
        }
    }
}