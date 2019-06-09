package com.rubyhuntersky.indexrebellion.interactions.main

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.stockcatalog.StockMarket

data class UpdateRebellionPrices(
    val rebellionBook: Book<Rebellion>,
    val stockMarketResult: StockMarket.Result
)