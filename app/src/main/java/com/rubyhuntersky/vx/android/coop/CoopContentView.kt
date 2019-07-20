package com.rubyhuntersky.vx.android.coop

import android.app.Activity
import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.Observable

class CoopContentView<in Sight : Any, Event : Any>(private val coop: Coop<Sight, Event>) : Vx<Sight, Event> {

    private lateinit var androidView: CoopAndroidView<Sight, Event>

    fun setInActivity(activity: Activity) {
        androidView = CoopAndroidView(activity, coop)
        activity.setContentView(androidView)
    }

    override val events: Observable<Event> get() = androidView.events
    override fun setSight(sight: Sight) = androidView.setSight(sight)
}