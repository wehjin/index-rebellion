package com.rubyhuntersky.indexrebellion.projections.holdings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSize
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.standardMarginSpan
import com.rubyhuntersky.vx.android.TowerAndroidViewHolder
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.margin.Margin
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleSight
import com.rubyhuntersky.vx.tower.additions.TitleSubtitleTower
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.margin.plusMargin
import com.rubyhuntersky.vx.tower.additions.pad.HPad
import com.rubyhuntersky.vx.tower.additions.pad.plusPad
import com.rubyhuntersky.vx.tower.additions.shareEnd
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import java.math.BigDecimal

class HoldingsActivity : AppCompatActivity() {

    private val page = Page(
        balance = "0,00",
        holdings = listOf(
            Holding(
                name = "Tesla, Inc.",
                custodians = listOf("Etrade", "Robinhood"),
                count = BigDecimal(10),
                symbol = "TSLA",
                value = BigDecimal(4200)
            )
        )
    )

    private data class Page(
        val balance: String,
        val holdings: List<Holding>
    )

    private data class Holding(
        val name: String,
        val custodians: List<String>,
        val count: BigDecimal,
        val symbol: String,
        val value: BigDecimal
    )

    private val standardMargin = Margin.Uniform(standardMarginSpan)
    private val balanceTower =
        TextWrapTower()
            .mapSight { page: Page ->
                TextWrapSight(page.balance, TextStyle.Highlight5)
            }
            .plusMargin(standardMargin)
            .plusPad(HPad.Uniform(standardMarginSize))

    private val holdingTower: Tower<Holding, Nothing> =
        TitleSubtitleTower
            .mapSight { holding: Holding ->
                TitleSubtitleSight(holding.name, holding.custodians.joinToString(", "))
            }
            .shareEnd(
                Span.Relative(0.5f),
                TitleSubtitleTower
                    .mapSight { holding: Holding ->
                        TitleSubtitleSight(
                            "${holding.count.toEngineeringString()} ${holding.symbol}",
                            "$${holding.value.toEngineeringString()}"
                        )
                    }
            )
            .plusMargin(standardMargin)

    private val pageTower: Tower<Page, Nothing> =
        balanceTower
            .extendFloor(
                holdingTower
                    .mapSight { page: Page ->
                        page.holdings.first()
                    }
            )


    private val towerAndroidViewHolder = TowerAndroidViewHolder(pageTower)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(towerAndroidViewHolder.setContext(this)) {
            setContentView(this)
            setSight(page)
        }
    }
}
