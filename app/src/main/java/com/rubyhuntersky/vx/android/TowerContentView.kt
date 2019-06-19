package com.rubyhuntersky.vx.android

import android.app.Activity
import com.rubyhuntersky.vx.tower.Tower

class TowerContentView<Sight : Any, Event : Any>(private val tower: Tower<Sight, Event>) {

    private lateinit var androidView: TowerAndroidView<Sight, Event>

    fun setInActivity(activity: Activity) {
        androidView = TowerAndroidView(activity, tower)
        activity.setContentView(androidView)
    }

    fun setSight(sight: Sight) = androidView.setSight(sight)
}