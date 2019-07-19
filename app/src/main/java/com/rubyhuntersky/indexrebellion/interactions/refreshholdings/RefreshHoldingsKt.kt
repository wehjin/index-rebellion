package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.rbhApi
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.presenters.main.toHoldings
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Lamp
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.api.RbhError
import com.rubyhuntersky.robinhood.api.results.RbhHoldingsResult
import com.rubyhuntersky.robinhood.login.ReadAccess
import com.rubyhuntersky.robinhood.login.RobinhoodLoginInteraction
import com.rubyhuntersky.robinhood.login.Action as RobinhoodLoginAction
import com.rubyhuntersky.robinhood.login.Vision as RobinhoodLoginVision

class RefreshHoldingsStory :
    Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, groupId) {

    companion object : InteractionCompanion<Vision, Action> {
        override val groupId: String = "RefreshHoldings"
    }
}

fun enableRefreshHoldings(lamp: Lamp) {
    with(lamp) {
        add(FetchRbhHoldingsGenie)
    }
}

sealed class Vision {

    object Idle : Vision()
    data class Loading(val api: RbhApi, val book: Book<Rebellion>) : Vision()
    data class AwaitingResult(val api: RbhApi, val book: Book<Rebellion>) : Vision()
    data class Error(val error: Throwable) : Vision()
    data class NewHoldings(val newHoldings: List<OwnedAsset>) : Vision()
}

private fun start(): Vision = Vision.Idle
private fun isEnding(maybe: Any?) = maybe is Vision.NewHoldings || maybe is Vision.Error


sealed class Action {
    data class Start(val api: RbhApi, val book: Book<Rebellion>) : Action()
    data class Load(val access: Access2) : Action()
    data class ReceiveError(val throwable: Throwable) : Action()
    data class ReceiveResult(val result: RbhHoldingsResult) : Action()
}

const val WRITE_ACCESS = "update-access-token"

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val readAccess = ReadAccess.toWish2(
                "read-access",
                onResult = Action::Load,
                onError = Action::ReceiveError
            )
            val loading = Vision.Loading(action.api, action.book)
            Revision(loading, Wish.none(WRITE_ACCESS), readAccess)
        }
        vision is Vision.Loading && action is Action.Load -> {
            val fetchHoldings = FetchRbhHoldingsGenie.wish(
                name = "holdings",
                params = FetchRbhHoldings(vision.api, action.access.token),
                resultToAction = Action::ReceiveResult,
                errorToAction = Action::ReceiveError
            )
            val awaiting = Vision.AwaitingResult(vision.api, vision.book)
            Revision(awaiting, fetchHoldings)
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
                    val loginWish = edge.wish(
                        name = WRITE_ACCESS,
                        interaction = RobinhoodLoginInteraction(),
                        startAction = RobinhoodLoginAction.Start(rbhApi),
                        endVisionToAction = {
                            val token = (it as RobinhoodLoginVision.Reporting).token
                            if (token.isNotBlank()) {
                                Action.Start(vision.api, vision.book)
                            } else {
                                Action.ReceiveError(action.throwable)
                            }
                        }
                    )
                    Revision(vision as Vision, loginWish)
                }
                else -> {
                    val error = Vision.Error(action.throwable) as Vision
                    Revision(error)
                }
            }
        }
        else -> throw IllegalStateException("$vision denies $action")
    }
}

