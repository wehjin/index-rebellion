package com.rubyhuntersky.indexrebellion.projections

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.additions.plusVMargin
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower

object Standard {

    const val marginSize: Int = 16
    val marginSpan = Span.Absolute(marginSize)
    val uniformMargin = Margin.Uniform(marginSpan)
    val uniformPad = HPad.Uniform(marginSize)

    class LabelTower<Sight : Any>(label: String) : Tower<Sight, Nothing> by WrapTextTower()
        .plusVMargin(uniformMargin)
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
}