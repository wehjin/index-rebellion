package com.rubyhuntersky.robinhood.api.results

data class RbhAccountsResult(
    val accountNumber: String,
    val cash: Double,
    val positionsLocation: String,
    val portfolioLocation: String,
    val accountLocation: String,
    val userLocation: String,
    val json: String
)