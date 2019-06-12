package com.rubyhuntersky.vx.dash.additions

import com.rubyhuntersky.vx.dash.Dash
import com.rubyhuntersky.vx.dash.dashes.TextLineDash
import com.rubyhuntersky.vx.dash.dashes.TextLineSight
import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.dash.dashes.TitleDash
import com.rubyhuntersky.vx.transform

operator fun TitleDash.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Dash<TitleSubtitleSight, Nothing> =
    TitleAtopSubtitleDash + Bottom(SubtitleBelowTitleDash) { Pair(it.title, it.subtitle) }

object Subtitle

data class TitleSubtitleSight(val title: String, val subtitle: String)

private object TitleAtopSubtitleDash :
    Dash<String, Nothing> by TextLineDash().transform({
        TextLineSight(
            it,
            TextStyle.Highlight6
        )
    })

private object SubtitleBelowTitleDash :
    Dash<String, Nothing> by TextLineDash().transform({
        TextLineSight(
            it,
            TextStyle.Subtitle1
        )
    })
