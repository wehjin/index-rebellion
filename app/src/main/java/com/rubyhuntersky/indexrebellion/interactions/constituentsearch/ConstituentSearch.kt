package com.rubyhuntersky.indexrebellion.interactions.constituentsearch

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.index.MarketWeight
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.SubjectInteraction
import com.rubyhuntersky.stockcatalog.StockCatalog
import com.rubyhuntersky.stockcatalog.StockCatalogClient
import com.rubyhuntersky.stockcatalog.StockCatalogQuery
import com.rubyhuntersky.stockcatalog.StockCatalogResult

typealias ConstituentSearchInteraction = Interaction<Vision, Action>

sealed class Vision {
    object Idle : Vision()
    data class Search(val searchTerm: String, val candidates: List<Candidate>?) : Vision()
    object Finished : Vision()
}

data class Candidate(val assetSymbol: AssetSymbol, val description: String, val marketWeight: MarketWeight)

sealed class Action {
    object ClearSearch : Action()
    data class BeginSearch(val newSearch: String) : Action()
    object AddConstituent : Action()
}

class ConstituentSearch(private val rebellionBook: Book<Rebellion>) : ConstituentSearchInteraction,
    SubjectInteraction<Vision, Action>(startVision = Vision.Idle, startAction = Action.ClearSearch),
    StockCatalogClient {

    override fun sendAction(action: Action) {
        when (action) {
            is Action.ClearSearch -> {
                stockCatalog.sendQuery(StockCatalogQuery.Clear)
                setVision(Vision.Idle)
            }
            is Action.BeginSearch -> {
                val newTerm = action.newSearch.trim().toLowerCase()
                if (newTerm.isBlank()) {
                    stockCatalog.sendQuery(StockCatalogQuery.Clear)
                    setVision(Vision.Idle)
                } else {
                    val vision = vision
                    val oldTerm = if (vision is Vision.Search) vision.searchTerm else ""
                    if (newTerm != oldTerm) {
                        setVision(
                            Vision.Search(
                                searchTerm = newTerm,
                                candidates = null
                            )
                        )
                        stockCatalog.sendQuery(StockCatalogQuery.FindStock(newTerm))
                    }
                }
            }
            is Action.AddConstituent -> {
                stockCatalog.sendQuery(StockCatalogQuery.Clear)
                val vision = vision
                val choice = if (vision is Vision.Search) vision.candidates?.firstOrNull() else null
                choice?.let {
                    val newRebellion = rebellionBook.value.addConstituent(it.assetSymbol, it.marketWeight)
                    rebellionBook.write(newRebellion)
                }
                setVision(Vision.Finished)
            }
        }
    }

    override lateinit var stockCatalog: StockCatalog

    override fun onStockCatalogResult(result: StockCatalogResult) {
        val vision = vision
        if (vision is Vision.Search) {
            val newVision = if (result is StockCatalogResult.Samples) {
                Vision.Search(
                    searchTerm = vision.searchTerm,
                    candidates = result.samples.map {
                        Candidate(
                            assetSymbol = AssetSymbol(it.symbol.trim().toUpperCase()),
                            description = it.issuer,
                            marketWeight = MarketWeight(it.marketCapitalization)
                        )
                    })
            } else {
                Vision.Search(
                    searchTerm = vision.searchTerm,
                    candidates = emptyList()
                )
            }
            setVision(newVision)
        }
    }
}
