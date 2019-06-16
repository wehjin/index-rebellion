package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.augment.HAugment
import com.rubyhuntersky.vx.tower.additions.augment.plusAugment
import com.rubyhuntersky.vx.tower.towers.TitleTower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower

object TitleSubtitleTower : Tower<TitleSubtitleSight, Nothing>
by TitleAtopSubtitleTower
    .mapSight(TitleSubtitleSight::title)
    .plusAugment(
        HAugment.Floor(
            SubtitleBelowTitleTower.mapSight(TitleSubtitleSight::subtitle)
        )
    )

private object TitleAtopSubtitleTower : Tower<String, Nothing>
by TextWrapTower().mapSight({ TextWrapSight(it, TextStyle.Highlight6) })

private object SubtitleBelowTitleTower : Tower<String, Nothing>
by TextWrapTower().mapSight({ TextWrapSight(it, TextStyle.Subtitle1) })

// TODO: Remove everything below here
@Deprecated(message = "Replace with TitleSubtitle")
operator fun TitleTower.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Tower<TitleSubtitleSight, Nothing> =
    TitleAtopSubtitleTower + Bottom(SubtitleBelowTitleTower) { Pair(it.title, it.subtitle) }

@Deprecated(message = "Replace with TitleSubtitle")
object Subtitle
