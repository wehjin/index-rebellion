package com.rubyhuntersky.indexrebellion

import com.rubyhuntersky.stockcatalog.MarketWeight
import com.rubyhuntersky.stockcatalog.StockSample
import java.math.BigDecimal

fun StockSample.toMacroValue(): BigDecimal = when (val weight = marketWeight) {
    is MarketWeight.Capitalization -> weight.marketCapitalization
    MarketWeight.None -> BigDecimal.ONE
}

