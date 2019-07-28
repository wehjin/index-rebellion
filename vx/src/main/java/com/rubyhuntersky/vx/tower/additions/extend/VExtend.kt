package com.rubyhuntersky.vx.tower.additions.extend

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.EmptyTower

sealed class VExtend<in Sight : Any, Event : Any> {

    abstract val ceilingTower: Tower<Sight, Event>
    abstract val floorTower: Tower<Sight, Event>

    data class Uniform<in Sight : Any, Event : Any>(val tower: Tower<Sight, Event>) : VExtend<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = tower
        override val floorTower: Tower<Sight, Event> = tower
    }

    data class Individual<in Sight : Any, Event : Any>(
        override val ceilingTower: Tower<Sight, Event>,
        override val floorTower: Tower<Sight, Event>
    ) : VExtend<Sight, Event>()

    data class Ceiling<in Sight : Any, Event : Any>(val tower: Tower<Sight, Event>) : VExtend<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = tower
        override val floorTower: Tower<Sight, Event> = EmptyTower()
    }

    data class Floor<in Sight : Any, Event : Any>(val tower: Tower<Sight, Event>) : VExtend<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = EmptyTower()
        override val floorTower: Tower<Sight, Event> = tower
    }

    class None<in Sight : Any, Event : Any> : VExtend<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = EmptyTower(0)
        override val floorTower: Tower<Sight, Event> = EmptyTower(0)
    }
}