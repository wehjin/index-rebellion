package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.presenters.main.toHoldings
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult


private fun start(): Vision = Vision.Idle

sealed class Vision {
    object Idle : Vision()
    data class AwaitingResult(val book: Book<Rebellion>) : Vision()
    data class Error(val error: Throwable) : Vision()
    data class NewHoldings(val newHoldings: List<OwnedAsset>) : Vision()
}

private fun isEnding(maybe: Any?) = maybe is Vision.NewHoldings || maybe is Vision.Error

private fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        vision is Vision.Idle && action is Action.Start -> {
            val holdingsWish = action.api.holdings(action.token)
                .toWish(
                    name = "holdings",
                    onSuccess = { Action.ReceiveResult(it) as Action },
                    onFailure = Action::ReceiveError
                )
            Revision(Vision.AwaitingResult(action.book), holdingsWish)
        }
        vision is Vision.AwaitingResult && action is Action.ReceiveResult -> {
            val newHoldings = action.result.toHoldings()
            val newRebellion = vision.book.value.withHoldings(newHoldings)
            vision.book.write(newRebellion)
            Revision(Vision.NewHoldings(newHoldings))
        }
        vision is Vision.AwaitingResult && action is Action.ReceiveError -> {
            Revision(Vision.Error(action.throwable))
        }
        else -> throw IllegalStateException("$vision denies $action")
    }
}

sealed class Action {
    data class Start(val token: String, val api: RbhApi, val book: Book<Rebellion>, val id: Long) : Action()
    data class ReceiveError(val throwable: Throwable) : Action()
    data class ReceiveResult(val result: RbhHoldingsResult) : Action()
}

class RefreshHoldingsStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, REFRESH_HOLDINGS)

const val REFRESH_HOLDINGS = "RefreshHoldings"
