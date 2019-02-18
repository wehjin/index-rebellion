package com.rubyhuntersky.vx.additions

import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.dashes.TextLine
import com.rubyhuntersky.vx.dashes.TextLineDash
import com.rubyhuntersky.vx.dashes.TextStyle
import com.rubyhuntersky.vx.dashes.TitleDash

operator fun TitleDash.plus(@Suppress("UNUSED_PARAMETER") subtitle: Subtitle): Dash<TitleSubtitle, Nothing> =
    TitleAtopSubtitleDash + Floor(SubtitleBelowTitleDash) { Pair(it.title, it.subtitle) }

object Subtitle
data class TitleSubtitle(val title: String, val subtitle: String)

private object TitleAtopSubtitleDash :
    Dash<String, Nothing> by TextLineDash().transform({
        TextLine(
            it,
            TextStyle.Highlight6
        )
    })

private object SubtitleBelowTitleDash :
    Dash<String, Nothing> by TextLineDash().transform({
        TextLine(
            it,
            TextStyle.Subtitle1
        )
    })
