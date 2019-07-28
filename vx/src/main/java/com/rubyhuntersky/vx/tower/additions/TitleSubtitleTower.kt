package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.extend.VExtend
import com.rubyhuntersky.vx.tower.additions.extend.extendVertical
import com.rubyhuntersky.vx.tower.towers.TitleTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower

object TitleSubtitleTower : Tower<TitleSubtitleSight, Nothing>
by TitleAtopSubtitleTower
    .mapSight(TitleSubtitleSight::title)
    .extendVertical(
        VExtend.Floor(
            SubtitleBelowTitleTower.mapSight(TitleSubtitleSight::subtitle)
        )
    )

private object TitleAtopSubtitleTower : Tower<String, Nothing>
by WrapTextTower().mapSight({ WrapTextSight(it, TextStyle.Highlight6) })

private object SubtitleBelowTitleTower : Tower<String, Nothing>
by WrapTextTower().mapSight({ WrapTextSight(it, TextStyle.Subtitle1) })

// TODO: Remove everything below here
@Deprecated(message = "Replace with TitleSubtitle")
operator fun TitleTower.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Tower<TitleSubtitleSight, Nothing> =
    TitleAtopSubtitleTower + Bottom(SubtitleBelowTitleTower) { Pair(it.title, it.subtitle) }

@Deprecated(message = "Replace with TitleSubtitle")
object Subtitle
