package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult
import io.reactivex.Single

object FetchRbhHoldingsGenie :
    Genie<FetchRbhHoldings, RbhHoldingsResult> {

    override val paramsClass: Class<FetchRbhHoldings> = FetchRbhHoldings::class.java

    override fun toSingle(params: FetchRbhHoldings): Single<RbhHoldingsResult> = params.api.holdings(params.token)
}