package com.rubyhuntersky.indexrebellion.projections

import com.rubyhuntersky.vx.android.toUnit
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
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.click.ClickTower
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower

object Standard {

    const val marginSize: Int = 16
    val marginSpan = Span.Absolute(marginSize)
    val uniformMargin = Margin.Uniform(marginSpan)
    val uniformPad = VPad.Uniform(marginSize)
    val centerClickPad = Margin.Uniform(Span.Relative(0.2f))

    class CenteredTextButton<Topic : Any> : Tower<ClickSight<Topic>, ClickEvent<Topic>>
    by ClickTower<Topic>()
        .plusHMargin(centerClickPad).plusVPad(uniformPad)

    class InsetTextInputTower<Topic : Any> : Tower<TextInputSight<Topic>, TextInputEvent<Topic>>
    by TextInputTower<Topic>()
        .plusHMargin(uniformMargin)

    class TitleTower(orbit: Orbit = Orbit.HeadLit) : Tower<String, Nothing>
    by WrapTextTower()
        .mapSight({
            WrapTextSight(it, TextStyle.Highlight5, orbit)
        })

    class SubtitleTower(orbit: Orbit = Orbit.HeadLit) : Tower<String, Nothing>
    by WrapTextTower()
        .mapSight({
            WrapTextSight(it, TextStyle.Subtitle1, orbit)
        })

    class ItemAttributeTower(orbit: Orbit = Orbit.HeadLit) : Tower<ClosedRange<String>, Nothing>
    by BodyTower(pad = false, orbit = orbit).mapSight(ClosedRange<String>::start)
        .extendFloor(
            SubtitleTower(orbit).mapSight(ClosedRange<String>::endInclusive)
        )

    class LabelTower(label: String) : Tower<Unit, Nothing>
    by WrapTextTower()
        .plusHMargin(uniformMargin)
        .mapSight({ WrapTextSight(label, TextStyle.Highlight5, Orbit.Center) })

    class BodyTower(pad: Boolean = true, orbit: Orbit = Orbit.HeadLit) : Tower<String, Nothing>
    by WrapTextTower()
        .let({ if (pad) it.plusHMargin(uniformMargin).plusVPad(uniformPad) else it })
        .mapSight({ WrapTextSight(it, TextStyle.Body1, orbit) })

    class SectionTower<Sight : Any, Event : Any>(
        vararg sections: Pair<String, Tower<Sight, Event>>
    ) : Tower<Sight, Event>
    by sections.fold(
        initial = EmptyTower<Sight, Event>() as Tower<Sight, Event>,
        operation = { tower, step ->
            val label = LabelTower(step.first).neverEvent<Event>()
                .mapSight<Unit, Event, Sight> { it.toUnit() }
            val body = step.second
            val section = body.extendCeiling(label)
            tower.extendFloor(section)
        }
    )
}