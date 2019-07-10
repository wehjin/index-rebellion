package com.rubyhuntersky.indexrebellion.projections

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.augment.extendCeiling
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.VPad
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.plusHMargin
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower

object Standard {

    const val marginSize: Int = 16
    val marginSpan = Span.Absolute(marginSize)
    val uniformMargin = Margin.Uniform(marginSpan)
    val uniformPad = VPad.Uniform(marginSize)
    val centerClickPad = Margin.Uniform(Span.Relative(0.2f))

    class TitleTower : Tower<String, Nothing> by WrapTextTower()
        .mapSight({ WrapTextSight(it, TextStyle.Highlight5) })

    class SubtitleTower : Tower<String, Nothing> by WrapTextTower()
        .mapSight({ WrapTextSight(it, TextStyle.Subtitle1) })

    class LabelTower<Sight : Any, Event : Any>(label: String) : Tower<Sight, Event> by WrapTextTower()
        .plusHMargin(uniformMargin)
        .neverEvent<Event>()
        .mapSight({ WrapTextSight(label, TextStyle.Highlight5, Orbit.Center) })

    class BodyTower(pad: Boolean = true) : Tower<String, Nothing> by WrapTextTower()
        .let({
            if (pad) {
                it.plusHMargin(uniformMargin).plusVPad(uniformPad)
            } else it
        })
        .mapSight({ WrapTextSight(it, TextStyle.Body1, Orbit.HeadLit) })

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