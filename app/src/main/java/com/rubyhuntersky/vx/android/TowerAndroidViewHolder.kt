package com.rubyhuntersky.vx.android

import android.content.Context
import com.rubyhuntersky.vx.tower.Tower

class TowerAndroidViewHolder<Sight : Any, Event : Any>(private val tower: Tower<Sight, Event>) {

    lateinit var item: TowerAndroidView<Sight, Event>

    fun setContext(context: Context): TowerAndroidView<Sight, Event> {
        item = TowerAndroidView(context, tower)
        return item
    }
}