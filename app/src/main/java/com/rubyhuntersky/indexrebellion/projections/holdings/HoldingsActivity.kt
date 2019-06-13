package com.rubyhuntersky.indexrebellion.projections.holdings

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.android.ScreenView
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.margin.Margin
import com.rubyhuntersky.vx.tower.additions.plus
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower

class HoldingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screen = ScreenView(this)
            .also {
                it.setBackgroundColor(Color.LTGRAY)
                setContentView(it)
            }

        val standardMarginSize = resources.getDimensionPixelSize(R.dimen.standard_margin_size)
        val standardMarginSpan = Span.Absolute(standardMarginSize)

        val tower = TextWrapTower()
            .plus(Margin.Uniform(standardMarginSpan))

        tower.enview(screen, ViewId())
            .also {
                it.setSight(TextWrapSight("0,00", TextStyle.Highlight5))
                screen.render(it)
            }
    }
}
