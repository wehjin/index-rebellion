package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.indexrebellion.common.MyApplication
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.presenters.main.toHoldings
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.api.RbhError
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult
import com.rubyhuntersky.robinhood.login.Vision as RobinhoodLoginVision

class RefreshHoldingsStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, REFRESH_HOLDINGS)

const val REFRESH_HOLDINGS = "RefreshHoldings"

sealed class Vision {

    object Idle : Vision()
    data class AwaitingResult(val api: RbhApi, val book: Book<Rebellion>) : Vision()
    data class Error(val error: Throwable) : Vision()
    data class NewHoldings(val newHoldings: List<OwnedAsset>) : Vision()
}

private fun start(): Vision = Vision.Idle
private fun isEnding(maybe: Any?) = maybe is Vision.NewHoldings || maybe is Vision.Error


sealed class Action {
    data class Start(val token: String, val api: RbhApi, val book: Book<Rebellion>) : Action()
    data class ReceiveError(val throwable: Throwable) : Action()
    data class ReceiveResult(val result: RbhHoldingsResult) : Action()
}

private fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val holdingsWish = action.api.holdings(action.token)
                .toWish(
                    name = "holdings",
                    onSuccess = { Action.ReceiveResult(it) as Action },
                    onFailure = Action::ReceiveError
                )
            val newVision = Vision.AwaitingResult(action.api, action.book)
            Revision(newVision, holdingsWish)
        }
        vision is Vision.AwaitingResult && action is Action.ReceiveResult -> {
            val newHoldings = action.result.toHoldings()
            val newRebellion = vision.book.value.withHoldings(newHoldings)
            vision.book.write(newRebellion)
            val newVision = Vision.NewHoldings(newHoldings)
            Revision(newVision)
        }
        vision is Vision.AwaitingResult && action is Action.ReceiveError -> {
            when (action.throwable) {
                is RbhError.Unauthorized -> {
                    val loginWish = MyApplication.robinhoodLoginInteraction()
                        .also { AndroidEdge.presentInteraction(it) }
                        .toWish("update-access") {
                            val token = (it as RobinhoodLoginVision.Reporting).token
                            if (token.isNotBlank()) {
                                Action.Start(token, vision.api, vision.book)
                            } else {
                                Action.ReceiveError(action.throwable)
                            }
                        }
                    Revision(vision as Vision, loginWish)
                }
                else -> {
                    val newVision = Vision.Error(action.throwable) as Vision
                    Revision(newVision)
                }
            }
        }
        else -> throw IllegalStateException("$vision denies $action")
    }
}

