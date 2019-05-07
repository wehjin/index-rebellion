package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import android.util.Log
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult

sealed class RefreshHoldingsVision {
    object Idle : RefreshHoldingsVision()
    object AwaitingResult : RefreshHoldingsVision()
    data class Error(val message: String) : RefreshHoldingsVision()
    data class Holdings(val holdings: RbhHoldingsResult) : RefreshHoldingsVision()
}

sealed class RefreshHoldingsAction {
    data class Start(val token: String, val api: RbhApi, val id: Long) : RefreshHoldingsAction()
    data class Error(val throwable: Throwable) : RefreshHoldingsAction()
    data class Result(val result: RbhHoldingsResult) : RefreshHoldingsAction()
}

const val REFRESH_HOLDINGS = "RefreshHoldings"

private typealias UpdateResult = WellResult<RefreshHoldingsVision, RefreshHoldingsAction>

private fun update(vision: RefreshHoldingsVision, action: RefreshHoldingsAction): UpdateResult {
    return if (vision is RefreshHoldingsVision.Idle && action is RefreshHoldingsAction.Start) {
        val holdingsWish = Wish<RefreshHoldingsAction>(
            action = action.api.holdings(action.token)
                .map(RefreshHoldingsAction::Result)
                .cast(RefreshHoldingsAction::class.java)
                .onErrorReturn(RefreshHoldingsAction::Error),
            name = "holdings${action.id}"
        )
        UpdateResult(RefreshHoldingsVision.AwaitingResult, holdingsWish)
    } else if (vision is RefreshHoldingsVision.AwaitingResult && action is RefreshHoldingsAction.Error) {
        val throwable = action.throwable
        Log.e(REFRESH_HOLDINGS, "Holdings fetch failed: ${throwable.localizedMessage}", throwable)
        UpdateResult(RefreshHoldingsVision.Error(throwable.localizedMessage))
    } else if (vision is RefreshHoldingsVision.AwaitingResult && action is RefreshHoldingsAction.Result) {
        UpdateResult(RefreshHoldingsVision.Holdings(action.result))
    } else {
        throw IllegalStateException("$vision denies $action")
    }
}

class RefreshHoldingsInteraction(well: Well) : Interaction<RefreshHoldingsVision, RefreshHoldingsAction>
by WellInteraction(well, { RefreshHoldingsVision.Idle }, ::update, { false }, REFRESH_HOLDINGS)