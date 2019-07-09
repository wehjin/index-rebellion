package com.rubyhuntersky.vx.tower.additions.pad

sealed class VPad {

    abstract val ceilingHeight: Int
    abstract val floorHeight: Int

    data class Uniform(val height: Int) : VPad() {
        override val ceilingHeight: Int = height
        override val floorHeight: Int = height
    }

    data class Individual(
        override val ceilingHeight: Int,
        override val floorHeight: Int
    ) : VPad()

    data class Ceiling(val height: Int) : VPad() {
        override val ceilingHeight: Int = height
        override val floorHeight: Int = 0
    }

    data class Floor(val height: Int) : VPad() {
        override val ceilingHeight: Int = 0
        override val floorHeight: Int = height
    }

    object None : VPad() {
        override val ceilingHeight: Int = 0
        override val floorHeight: Int = 0
    }
}