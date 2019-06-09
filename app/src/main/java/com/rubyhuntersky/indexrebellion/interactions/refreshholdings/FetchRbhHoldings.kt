package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.robinhood.api.RbhApi

data class FetchRbhHoldings(
    val api: RbhApi,
    val token: String
)