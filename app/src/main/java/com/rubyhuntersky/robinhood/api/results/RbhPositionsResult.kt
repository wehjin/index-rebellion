package com.rubyhuntersky.robinhood.api.results

data class RbhPositionsResult(
    val quantity: Double,
    val instrumentLocation: String,
    val averagePrice: Double,
    val json: String
)