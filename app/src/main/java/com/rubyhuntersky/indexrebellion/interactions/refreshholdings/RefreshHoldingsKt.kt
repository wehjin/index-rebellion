package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.accessBook
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
import com.rubyhuntersky.robinhood.login.RobinhoodLoginInteraction
import com.rubyhuntersky.robinhood.login.Services
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

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> = when {
    action is Action.Start -> {
        val api = action.api
        val newVision = Vision.AwaitingResult(api, action.book)
        val wish = FetchRbhHoldingsGenie.wish(
            name = "holdings",
            params = FetchRbhHoldings(api, action.token),
            resultToAction = Action::ReceiveResult,
            errorToAction = Action::ReceiveError
        )
        Revision(newVision, wish, Wish.none("update-access-token"))
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
                    name = "update-access-token",
                    interaction = RobinhoodLoginInteraction(),
                    startAction = RobinhoodLoginAction.Start(Services(rbhApi, accessBook)),
                    endVisionToAction = {
                        val token = (it as RobinhoodLoginVision.Reporting).token
                        if (token.isNotBlank()) {
                            Action.Start(token, vision.api, vision.book)
                        } else {
                            Action.ReceiveError(action.throwable)
                        }
                    }
                )
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

