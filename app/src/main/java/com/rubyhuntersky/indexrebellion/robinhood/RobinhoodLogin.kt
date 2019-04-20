package com.rubyhuntersky.indexrebellion.robinhood

import com.rubyhuntersky.interaction.core.BehaviorInteraction
import io.reactivex.disposables.Disposable

sealed class Vision {
    object Idle : Vision()
    data class Editing(val username: String, val password: String, val error: String?, val submittable: Boolean) :
        Vision()

    data class Submitting(val username: String, val password: String) : Vision()
    data class Reporting(val username: String, val token: String) : Vision()
}

sealed class Action {
    data class Start(val username: String?) : Action()
    data class SetUsername(val username: String) : Action()
    data class SetPassword(val password: String) : Action()
    object Submit : Action()
    data class Succeed(val token: String) : Action()
    data class Fail(val throwable: Throwable) : Action()
    object Cancel : Action()
}

class RobinhoodLoginInteraction(private val robinhoodApi: RobinhoodApi) :
    BehaviorInteraction<Vision, Action>(startVision = Vision.Idle) {

    override fun sendAction(action: Action) {
        var afterUpdate: (() -> Unit)? = null
        endLogin()
        val state = state
        this.state = when (action) {
            is Action.Start -> State.Editing(
                partialUsername = action.username ?: state.nearestUsername,
                partialPassword = state.nearestPassword,
                error = ""
            )
            is Action.SetUsername -> State.Editing(
                partialUsername = action.username,
                partialPassword = state.nearestPassword,
                error = state.nearestError
            )
            is Action.SetPassword -> State.Editing(
                partialUsername = state.nearestUsername,
                partialPassword = action.password,
                error = state.nearestError
            )
            is Action.Submit -> {
                if (state is State.Editing && state.isSubmittable) {
                    val username = state.partialUsername
                    val password = state.partialPassword
                    afterUpdate = { startLogin(username, password) }
                    State.Verifying(possibleUsername = username, possiblePassword = password)
                } else {
                    state
                }
            }
            is Action.Succeed -> State.Reporting(state.nearestUsername, action.token)
            is Action.Fail -> State.Editing(
                partialUsername = state.nearestUsername,
                partialPassword = state.nearestPassword,
                error = action.throwable.localizedMessage
            )
            is Action.Cancel -> {
                when (state) {
                    is State.Verifying -> State.Editing(
                        partialUsername = state.possibleUsername,
                        partialPassword = state.possiblePassword,
                        error = ""
                    )
                    else -> State.Reporting(verifiedUsername = "", token = "")
                }
            }
        }
        setVision(state.toVision())
        afterUpdate?.invoke()
    }

    private fun startLogin(username: String, password: String) {
        loginDisposable = robinhoodApi.login(username, password)
            .map<Action>(Action::Succeed)
            .onErrorReturn(Action::Fail)
            .subscribe(this::sendAction)
    }

    private fun endLogin() {
        loginDisposable?.dispose()
    }

    private var loginDisposable: Disposable? = null

    private var state: State = State.Idle

    private sealed class State {
        abstract fun toVision(): Vision

        object Idle : State() {
            override fun toVision(): Vision {
                return Vision.Idle
            }
        }

        data class Editing(val partialUsername: String, val partialPassword: String, val error: String) : State() {

            override fun toVision(): Vision = Vision.Editing(partialUsername, partialPassword, error, isSubmittable)

            val isSubmittable: Boolean
                get() = partialUsername.isNotBlank() && partialPassword.isNotBlank()
        }

        data class Verifying(val possibleUsername: String, val possiblePassword: String) : State() {
            override fun toVision(): Vision = Vision.Submitting(possibleUsername, possiblePassword)
        }

        data class Reporting(val verifiedUsername: String, val token: String) : State() {
            override fun toVision(): Vision = Vision.Reporting(verifiedUsername, token)
        }

        val nearestError: String
            get() = when (this) {
                is Editing -> error
                else -> ""
            }

        val nearestUsername: String
            get() = when (this) {
                is Idle -> ""
                is Editing -> partialUsername
                is Verifying -> possibleUsername
                is Reporting -> verifiedUsername
            }

        val nearestPassword: String
            get() = when (this) {
                is Editing -> partialPassword
                is Verifying -> possiblePassword
                else -> ""
            }
    }
}
