package com.rubyhuntersky.robinhood.api

data class RbhPositionsResult(
    val quantity: Double,
    val instrumentLocation: String,
    val averagePrice: Double,
    val json: String
)