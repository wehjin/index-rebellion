package com.rubyhuntersky.robinhood.api.results

data class RbhQuotesResult(
    val symbol: String,
    val lastPrice: Double,
    val updatedAt: String,
    val instrumentLocation: String,
    val json: String
)