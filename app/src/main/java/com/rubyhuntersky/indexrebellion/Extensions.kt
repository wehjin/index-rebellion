package com.rubyhuntersky.indexrebellion

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate

fun Plate.toLabel(): String = when (this) {
    Plate.Unknown -> "Unclassified"
    Plate.Fiat -> "Cash"
    Plate.BlockChain -> "Coin"
    Plate.Debt -> "Debt"
    Plate.GlobalEquity -> "Global Stocks"
    Plate.ZonalEquity -> "Zonal Stocks"
    Plate.LocalEquity -> "Local Stocks"
}
