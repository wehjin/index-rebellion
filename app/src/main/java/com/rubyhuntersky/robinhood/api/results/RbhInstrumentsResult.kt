package com.rubyhuntersky.robinhood.api.results

data class RbhInstrumentsResult(
    val fundamentalsLocation: String,
    val id: String,
    val marketLocation: String,
    val quoteLocation: String,
    val name: String,
    val simpleName: String,
    val symbol: String,
    val type: String,
    val json: String
)