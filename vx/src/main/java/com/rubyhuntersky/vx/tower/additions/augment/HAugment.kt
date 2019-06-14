package com.rubyhuntersky.vx.tower.additions.augment

import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.EmptyTower

sealed class HAugment<Sight : Any, Event : Any> {

    abstract val ceilingAugment: Augment<Sight, Event>
    abstract val floorAugment: Augment<Sight, Event>

    data class Uniform<Sight : Any, Event : Any>(
        val span: Span,
        val tower: Tower<Sight, Event> = EmptyTower()
    ) : HAugment<Sight, Event>() {
        override val ceilingAugment: Augment<Sight, Event> =
            Augment(span, tower)
        override val floorAugment: Augment<Sight, Event> =
            Augment(span, tower)
    }

    data class Individual<Sight : Any, Event : Any>(
        override val ceilingAugment: Augment<Sight, Event>,
        override val floorAugment: Augment<Sight, Event>
    ) : HAugment<Sight, Event>() {
        constructor(
            ceilingSpan: Span,
            floorSpan: Span,
            ceilingTower: Tower<Sight, Event> = EmptyTower(),
            floorTower: Tower<Sight, Event> = EmptyTower()
        ) : this(
            ceilingAugment = Augment(ceilingSpan, ceilingTower),
            floorAugment = Augment(floorSpan, floorTower)
        )
    }

    data class Ceiling<Sight : Any, Event : Any>(
        val span: Span,
        val tower: Tower<Sight, Event> = EmptyTower()
    ) : HAugment<Sight, Event>() {
        override val ceilingAugment: Augment<Sight, Event> =
            Augment(span, tower)
        override val floorAugment: Augment<Sight, Event> =
            Augment(Span.None, EmptyTower())
    }

    data class Floor<Sight : Any, Event : Any>(
        val span: Span,
        val tower: Tower<Sight, Event> = EmptyTower()
    ) : HAugment<Sight, Event>() {
        override val ceilingAugment: Augment<Sight, Event> =
            Augment(Span.None, EmptyTower())
        override val floorAugment: Augment<Sight, Event> =
            Augment(span, tower)
    }

    class Empty<Sight : Any, Event : Any> : HAugment<Sight, Event>() {
        override val ceilingAugment: Augment<Sight, Event> = Augment.empty()
        override val floorAugment: Augment<Sight, Event> = Augment.empty()
    }
}