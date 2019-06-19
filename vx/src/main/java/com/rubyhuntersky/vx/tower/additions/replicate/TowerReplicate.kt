package com.rubyhuntersky.vx.tower.additions.replicate

import com.rubyhuntersky.vx.common.Ranked
import com.rubyhuntersky.vx.tower.Tower

fun <Sight : Any, Event : Any> Tower<Sight, Event>.replicate(): Tower<List<Sight>, Ranked<Event>> = ReplicateTower(this)
