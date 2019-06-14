package com.rubyhuntersky.vx.tower.additions.haugment

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.EmptyTower

sealed class HAugment<Sight : Any, Event : Any> {

    abstract val ceilingTower: Tower<Sight, Event>
    abstract val floorTower: Tower<Sight, Event>

    data class Uniform<Sight : Any, Event : Any>(
        val tower: Tower<Sight, Event>
    ) : HAugment<Sight, Event>() {
        constructor(height: Int) : this(EmptyTower<Sight, Event>(height))

        override val ceilingTower: Tower<Sight, Event> = tower
        override val floorTower: Tower<Sight, Event> = tower
    }

    data class Individual<Sight : Any, Event : Any>(
        override val ceilingTower: Tower<Sight, Event>,
        override val floorTower: Tower<Sight, Event>
    ) : HAugment<Sight, Event>() {
        constructor(ceilingHeight: Int, floorHeight: Int) : this(
            EmptyTower<Sight, Event>(ceilingHeight),
            EmptyTower(floorHeight)
        )
    }

    data class Ceiling<Sight : Any, Event : Any>(
        val tower: Tower<Sight, Event>
    ) : HAugment<Sight, Event>() {
        constructor(height: Int) : this(EmptyTower<Sight, Event>(height))

        override val ceilingTower: Tower<Sight, Event> = tower
        override val floorTower: Tower<Sight, Event> = EmptyTower()
    }

    data class Floor<Sight : Any, Event : Any>(
        val tower: Tower<Sight, Event>
    ) : HAugment<Sight, Event>() {
        constructor(height: Int) : this(EmptyTower<Sight, Event>(height))

        override val ceilingTower: Tower<Sight, Event> = EmptyTower()
        override val floorTower: Tower<Sight, Event> = tower
    }

    class None<Sight : Any, Event : Any> : HAugment<Sight, Event>() {
        override val ceilingTower: Tower<Sight, Event> = EmptyTower(0)
        override val floorTower: Tower<Sight, Event> = EmptyTower(0)
    }
}