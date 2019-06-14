package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSpan
import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.android.TowerAndroidViewHolder
import com.rubyhuntersky.vx.margin.Margin
import com.rubyhuntersky.vx.tower.additions.plus
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower

class HoldingsActivity : AppCompatActivity() {

    private val tower = TextWrapTower().plus(Margin.Uniform(standardMarginSpan))
    private val towerAndroidViewHolder = TowerAndroidViewHolder(tower)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(towerAndroidViewHolder.setContext(this)) {
            setContentView(this)
            setSight(TextWrapSight("0,00", TextStyle.Highlight5))
        }
    }
}
