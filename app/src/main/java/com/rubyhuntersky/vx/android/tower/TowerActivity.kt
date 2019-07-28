package com.rubyhuntersky.vx.android.tower

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ScrollView
import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.tower.Tower

abstract class TowerActivity<Sight : Any, Event : Any> : AppCompatActivity() {

    abstract val activityTower: Tower<Sight, Event>

    lateinit var vx: Vx<Sight, Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidTowerViewHost(this@TowerActivity, activityTower)
            .also {
                vx = it
                setContentView(ScrollView(this).apply { addView(it, MATCH_PARENT, WRAP_CONTENT) })
            }
    }
}
