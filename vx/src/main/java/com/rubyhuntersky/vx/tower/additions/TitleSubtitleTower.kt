package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextTower

object TitleSubtitleTower : Tower<TitleSubtitleSight, Nothing>
by TitleAtopSubtitleTower.mapSight(TitleSubtitleSight::title).let({
    val subtitle = SubtitleBelowTitleTower.mapSight(TitleSubtitleSight::subtitle)
    it and subtitle
})

private object TitleAtopSubtitleTower :
    Tower<String, Nothing> by WrapTextTower().mapSight({ WrapTextSight(it, TextStyle.Highlight6) })

private object SubtitleBelowTitleTower :
    Tower<String, Nothing> by WrapTextTower().mapSight({ WrapTextSight(it, TextStyle.Subtitle1) })
