package com.rubyhuntersky.indexrebellion.interactions.constituentsearch

import com.nhaarman.mockitokotlin2.*
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.stockcatalog.*
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.math.BigDecimal

class ConstituentSearchTest {

    private val rebellionBehavior = BehaviorSubject.createDefault(Rebellion())

    private val rebellionBook = mock<RebellionBook> {
        on { reader } doReturn rebellionBehavior
        on { value } doReturn rebellionBehavior.value!!
    }

    private val interaction = ConstituentSearch(rebellionBook)

    @Test
    fun startingVisionIsIdle() {
        interaction.visions.test()
            .assertSubscribed()
            .assertValue(Vision.Idle)
            .assertNoErrors()
            .assertNotComplete()
    }

    @Test
    fun saveUpdatesRebellionBook() {
        val network = mock<HttpNetwork> {
            on { request(any()) } doAnswer {
                Thread.sleep(1000)
                HttpNetworkResponse.ConnectionError("url", Exception("mocked"))
            }
        }
        StockCatalog(network).connect(interaction)
        interaction.sendAction(Action.BeginSearch("TSLA"))
        interaction.onStockCatalogResult(
            StockCatalogResult.Samples(
                search = "TSLA",
                samples = listOf(
                    StockSample(
                        symbol = "TSLA",
                        sharePrice = BigDecimal.ONE,
                        marketCapitalization = BigDecimal.TEN,
                        issuer = "Tesla Motors"
                    )
                )
            )
        )
        interaction.sendAction(Action.AddConstituent)

        argumentCaptor<Rebellion>().apply {
            verify(rebellionBook).write(capture())
            assertNotNull(firstValue.index.constituents.find { it.assetSymbol == AssetSymbol("TSLA") })
        }
    }
}