package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult


private fun start(): Vision = Vision.Idle

sealed class Vision {
    object Idle : Vision()
    object AwaitingResult : Vision()
    data class Error(val message: String) : Vision()
    data class Holdings(val holdings: RbhHoldingsResult) : Vision()
}

private fun isTail(_: Any?) = false

private fun update(vision: Vision, action: Action): WellResult<Vision, Action> {
    return when {
        vision is Vision.Idle && action is Action.Start -> {
            val holdingsWish = Wish<Action>(
                action = action.api.holdings(action.token)
                    .map { Action.ReceiveResult(it) as Action }
                    .onErrorReturn(Action::ReceiveError),
                name = "holdings${action.id}"
            )
            WellResult(Vision.AwaitingResult, holdingsWish)
        }
        vision is Vision.AwaitingResult && action is Action.ReceiveError -> {
            WellResult(Vision.Error(action.throwable.localizedMessage))
        }
        vision is Vision.AwaitingResult && action is Action.ReceiveResult -> {
            WellResult(Vision.Holdings(action.result))
        }
        else -> throw IllegalStateException("$vision denies $action")
    }
}

sealed class Action {
    data class Start(val token: String, val api: RbhApi, val id: Long) : Action()
    data class ReceiveError(val throwable: Throwable) : Action()
    data class ReceiveResult(val result: RbhHoldingsResult) : Action()
}

class RefreshHoldingsInteraction(well: Well) : Interaction<Vision, Action>
by WellInteraction(well, ::start, ::update, ::isTail, REFRESH_HOLDINGS)

const val REFRESH_HOLDINGS = "RefreshHoldings"
