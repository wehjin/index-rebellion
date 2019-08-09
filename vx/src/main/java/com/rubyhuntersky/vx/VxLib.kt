package com.rubyhuntersky.vx

import io.reactivex.Observable
import java.text.DecimalFormat

interface Vx<in Sight : Any, Event : Any> {
    val events: Observable<Event>
    fun setSight(sight: Sight)
}

fun Double.toPercent(): String = percent.format(this)

private val percent = DecimalFormat("##.##%")
