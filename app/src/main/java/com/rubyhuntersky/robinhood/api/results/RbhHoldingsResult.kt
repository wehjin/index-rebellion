package com.rubyhuntersky.robinhood.api.results

class RbhHoldingsResult(val positions: List<RbhPositionsResult>) {
    lateinit var quotes: List<RbhQuotesResult>

    val quotesByInstrumentLocation: Map<String, RbhQuotesResult> by lazy { quotes.associateBy { it.instrumentLocation } }

    override fun toString(): String {
        return "RbhHoldingsResult(positions=$positions, quotes=$quotes)"
    }
}