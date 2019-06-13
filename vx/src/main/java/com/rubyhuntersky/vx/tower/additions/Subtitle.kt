package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrap
import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.tower.towers.TitleTower
import com.rubyhuntersky.vx.transform

operator fun TitleTower.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Tower<TitleSubtitleSight, Nothing> =
    TitleAtopSubtitleTower + Bottom(SubtitleBelowTitleTower) { Pair(it.title, it.subtitle) }

object Subtitle

data class TitleSubtitleSight(val title: String, val subtitle: String)

private object TitleAtopSubtitleTower :
    Tower<String, Nothing> by TextWrapTower().transform({
        TextWrap(
            it,
            TextStyle.Highlight6
        )
    })

private object SubtitleBelowTitleTower :
    Tower<String, Nothing> by TextWrapTower().transform({
        TextWrap(
            it,
            TextStyle.Subtitle1
        )
    })
