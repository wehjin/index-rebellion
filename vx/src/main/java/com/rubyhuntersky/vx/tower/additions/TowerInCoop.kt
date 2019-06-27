package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.coop.coops.TowerCoop
import com.rubyhuntersky.vx.tower.Tower

fun <Sight : Any, Event : Any> Tower<Sight, Event>.inCoop(): Coop<Sight, Event> = TowerCoop(this)