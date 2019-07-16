package com.rubyhuntersky.indexrebellion

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.stockcatalog.MarketWeight
import com.rubyhuntersky.stockcatalog.StockSample
import java.math.BigDecimal

fun Plate.toLabel(): String = when (this) {
    Plate.Unknown -> "Unclassified"
    Plate.Fiat -> "Cash"
    Plate.BlockChain -> "Coin"
    Plate.Debt -> "Debt"
    Plate.GlobalEquity -> "Global Stocks"
    Plate.ZonalEquity -> "Zonal Stocks"
    Plate.LocalEquity -> "Local Stocks"
}

fun StockSample.toMacroValue(): BigDecimal = when (val weight = marketWeight) {
    is MarketWeight.Capitalization -> weight.marketCapitalization
    MarketWeight.None -> BigDecimal.ONE
}

