package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSize
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSpan
import com.rubyhuntersky.vx.android.TowerAndroidViewHolder
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.margin.plusMargin
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusPad
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower

class HoldingsActivity : AppCompatActivity() {

    private val tower = TextWrapTower()
        .mapSight { text: String -> TextWrapSight(text, TextStyle.Highlight5) }
        .plusMargin(Margin.Uniform(standardMarginSpan))
        .plusPad(HPad.Uniform(standardMarginSize))

    private val towerAndroidViewHolder = TowerAndroidViewHolder(tower)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(towerAndroidViewHolder.setContext(this)) {
            setContentView(this)
            setSight("0,00")
        }
    }
}
