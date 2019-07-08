package com.rubyhuntersky.vx.android

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import kotlin.math.roundToInt


fun View.toPixels(dip: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), resources.displayMetrics)
}

fun View.toDip(px: Int): Int {
    return (px / (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun <Sight : Any, Event : Any> Tower<Sight, Event>.logEvents(tag: String): Tower<Sight, Event> {

    val core = this

    return object : Tower<Sight, Event> {
        override fun enview(viewHost: Tower.ViewHost, id: ViewId): Tower.View<Sight, Event> {

            val view = core.enview(viewHost, id)

            return object : Tower.View<Sight, Event> {

                override val events: Observable<Event>
                    get() {
                        Log.v(tag, "Retrieved EVENTS")
                        return view.events.doOnNext { Log.v(tag, "EVENT: $it") }
                    }

                override fun setSight(sight: Sight) {
                    view.setSight(sight)
                }

                override fun setHBound(hbound: HBound) {
                    view.setHBound(hbound)
                }

                override val latitudes: Observable<Latitude>
                    get() = view.latitudes

                override fun setAnchor(anchor: Anchor) {
                    view.setAnchor(anchor)
                }
            }
        }
    }
}

fun <Vision : Any, Action : Any> Interaction<Vision, Action>.logChanges(tag: String): Interaction<Vision, Action> {
    val core = this
    return object : Interaction<Vision, Action> {

        override val group: String
            get() = core.group

        override var edge: Edge
            get() = core.edge
            set(value) {
                core.edge = value
            }

        override val visions: Observable<Vision>
            get() = core.visions.doOnNext { println("$tag NEW VISION: $it") }

        override fun sendAction(action: Action) {
            println("$tag ACTION: $action VISION: ${core.visions.blockingFirst()}")
            core.sendAction(action)
        }
    }
}
