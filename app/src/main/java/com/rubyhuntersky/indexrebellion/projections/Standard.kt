package com.rubyhuntersky.indexrebellion.projections

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.augment.extendCeiling
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.additions.plusVMargin
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower

object Standard {

    const val marginSize: Int = 16
    val marginSpan = Span.Absolute(marginSize)
    val uniformMargin = Margin.Uniform(marginSpan)
    val uniformPad = HPad.Uniform(marginSize)

    class LabelTower<Sight : Any, Event : Any>(label: String) : Tower<Sight, Event> by WrapTextTower()
        .plusVMargin(uniformMargin)
        .neverEvent<Event>()
        .mapSight({
            WrapTextSight(
                label,
                TextStyle.Highlight5,
                Orbit.Center
            )
        })

    class BodyTower : Tower<String, Nothing> by WrapTextTower()
        .plusVMargin(uniformMargin)
        .plusHPad(uniformPad)
        .mapSight({
            WrapTextSight(
                it,
                TextStyle.Body1,
                Orbit.HeadLit
            )
        })

    class SectionTower<Sight : Any, Event : Any>(
        vararg sections: Pair<String, Tower<Sight, Event>>
    ) :
        Tower<Sight, Event>
        by sections.fold(
            EmptyTower<Sight, Event>() as Tower<Sight, Event>,
            { tower, step ->
                val label = LabelTower<Sight, Event>(step.first)
                val body = step.second
                val section = body.extendCeiling(label)
                tower.extendFloor(section)
            })
}