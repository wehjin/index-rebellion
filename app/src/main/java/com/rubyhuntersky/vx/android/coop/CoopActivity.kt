package com.rubyhuntersky.vx.android.coop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.coop.Coop

abstract class CoopActivity<Sight : Any, Event : Any> :
    AppCompatActivity() {

    protected abstract val activityCoop: Coop<Sight, Event>
    protected lateinit var vx: Vx<Sight, Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vx = CoopContentView(activityCoop).apply { setInActivity(this@CoopActivity) }
    }
}
