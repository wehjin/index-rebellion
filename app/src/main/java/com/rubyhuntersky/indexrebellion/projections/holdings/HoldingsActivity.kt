package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSize
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSpan
import com.rubyhuntersky.vx.android.TowerContentView
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleSight
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleTower
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.margin.plusVMargin
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusHPad
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.additions.shareEnd
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailSight
import com.rubyhuntersky.vx.tower.towers.detailsubdetail.DetailSubdetailTower
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower
import java.math.BigDecimal

class HoldingsActivity : AppCompatActivity() {

    private val page = PageSight(
        balance = "0,00",
        holdings = listOf(
            HoldingSight(
                name = "Tesla, Inc.",
                custodians = listOf("Etrade", "Robinhood"),
                count = BigDecimal.valueOf(10),
                symbol = "TSLA",
                value = BigDecimal.valueOf(4200)
            ),
            HoldingSight(
                name = "Square, Inc.",
                custodians = listOf("Sovereign"),
                count = BigDecimal.valueOf(100),
                symbol = "SQ",
                value = BigDecimal.valueOf(10000)
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(pageContentView) {
            setInActivity(this@HoldingsActivity)
            setSight(page)
        }
    }

    private val pageContentView = TowerContentView(pageTower)

    companion object {
        private val standardUniformMargin = Margin.Uniform(standardMarginSpan)

        private val balanceTower =
            WrapTextTower()
                .mapSight { page: PageSight ->
                    WrapTextSight(page.balance, TextStyle.Highlight5, Orbit.Center)
                }
                .plusVMargin(standardUniformMargin)
                .plusHPad(HPad.Uniform(standardMarginSize))

        private val holdingTower: Tower<HoldingSight, Nothing> =
            TitleSubtitleTower
                .mapSight { holding: HoldingSight ->
                    TitleSubtitleSight(holding.name, holding.custodians.joinToString(", "))
                }
                .shareEnd(
                    Span.Relative(0.5f),
                    DetailSubdetailTower
                        .mapSight { holding: HoldingSight ->
                            DetailSubdetailSight(
                                "${holding.count.toEngineeringString()} ${holding.symbol}",
                                "$${holding.value.toEngineeringString()}"
                            )
                        }
                )
                .plusVMargin(standardUniformMargin)
                .plusHPad(HPad.Uniform(standardMarginSize))

        private val pageTower: Tower<PageSight, Nothing> = balanceTower
            .extendFloor(
                holdingTower.replicate()
                    .mapSight { page: PageSight -> page.holdings }
                    .neverEvent()
            )
    }
}
