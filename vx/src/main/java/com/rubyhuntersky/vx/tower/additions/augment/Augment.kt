package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.EmptyTower

data class Augment<Sight : Any, Event : Any>(
    val span: Span,
    val tower: Tower<Sight, Event>
) {
    companion object {
        fun <Sight : Any, Event : Any> empty(): Augment<Sight, Event> = Augment(Span.None, EmptyTower())
    }
}