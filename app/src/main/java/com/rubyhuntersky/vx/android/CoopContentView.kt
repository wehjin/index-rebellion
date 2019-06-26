package com.rubyhuntersky.vx.android

import android.app.Activity
import com.rubyhuntersky.vx.coop.Coop

class CoopContentView<Sight : Any, Event : Any>(private val coop: Coop<Sight, Event>) {

    private lateinit var androidView: CoopAndroidView<Sight, Event>

    fun setInActivity(activity: Activity) {
        androidView = CoopAndroidView(activity, coop)
        activity.setContentView(androidView)
    }

    fun setSight(sight: Sight) = androidView.setSight(sight)
}