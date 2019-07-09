package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.EmptyTower

sealed class VAugment<Sight : Any, Event : Any> {

    abstract val ceilingTower: Tower<Sight, Event>
    abstract val floorTower: Tower<Sight, Event>

    data class Uniform<Sight : Any, Event : Any>(val tower: Tower<Sight, Event>) : VAugment<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = tower
        override val floorTower: Tower<Sight, Event> = tower
    }

    data class Individual<Sight : Any, Event : Any>(
        override val ceilingTower: Tower<Sight, Event>,
        override val floorTower: Tower<Sight, Event>
    ) : VAugment<Sight, Event>()

    data class Ceiling<Sight : Any, Event : Any>(val tower: Tower<Sight, Event>) : VAugment<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = tower
        override val floorTower: Tower<Sight, Event> = EmptyTower()
    }

    data class Floor<Sight : Any, Event : Any>(val tower: Tower<Sight, Event>) : VAugment<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = EmptyTower()
        override val floorTower: Tower<Sight, Event> = tower
    }

    class None<Sight : Any, Event : Any> : VAugment<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = EmptyTower(0)
        override val floorTower: Tower<Sight, Event> = EmptyTower(0)
    }
}