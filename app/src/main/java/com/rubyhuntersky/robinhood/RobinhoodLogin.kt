package com.rubyhuntersky.robinhood

import com.rubyhuntersky.interaction.core.BehaviorInteraction
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

sealed class Vision {
    data class Editing(
        val username: String,
        val password: String,
        val error: String,
        val submittable: Boolean,
        val mfa: String
    ) : Vision()

    data class Submitting(val username: String, val password: String, val mfa: String) : Vision()
    data class Reporting(val username: String, val token: String) : Vision()
}

sealed class Action {
    data class Start(val username: String?) : Action()
    data class SetUsername(val username: String) : Action()
    data class SetPassword(val password: String) : Action()
    data class SetMfa(val mfa: String) : Action()
    object Submit : Action()
    data class Succeed(val token: String) : Action()
    data class Fail(val throwable: Throwable) : Action()
    object Cancel : Action()
}

class RobinhoodLoginInteraction(private val robinhoodApi: RobinhoodApi) :
    BehaviorInteraction<Vision, Action>(startVision = Vision.Editing("", "", "", false, "")) {

    override fun sendAction(action: Action) {
        var afterUpdate: (() -> Unit)? = null
        endLogin()
        val oldState = state
        this.state = when (action) {
            is Action.Start -> {
                State.Editing(
                    partialUsername = action.username ?: oldState.nearestUsername,
                    partialPassword = oldState.nearestPassword,
                    error = "",
                    partialMfa = oldState.nearestMfa
                )
            }
            is Action.SetUsername -> {
                State.Editing(
                    partialUsername = action.username,
                    partialPassword = oldState.nearestPassword,
                    error = oldState.nearestError,
                    partialMfa = oldState.nearestMfa
                )
            }
            is Action.SetPassword -> {
                State.Editing(
                    partialUsername = oldState.nearestUsername,
                    partialPassword = action.password,
                    error = oldState.nearestError,
                    partialMfa = oldState.nearestMfa
                )
            }
            is Action.SetMfa -> {
                State.Editing(
                    partialUsername = oldState.nearestUsername,
                    partialPassword = oldState.nearestPassword,
                    error = oldState.nearestError,
                    partialMfa = action.mfa
                )
            }
            is Action.Submit -> {
                if (oldState is State.Editing && oldState.isSubmittable) {
                    val username = oldState.partialUsername
                    val password = oldState.partialPassword
                    val mfa = oldState.partialMfa
                    afterUpdate = { startLogin(username, password, mfa) }
                    State.Verifying(
                        possibleUsername = username,
                        possiblePassword = password,
                        possibleMfa = mfa
                    )
                } else {
                    oldState
                }
            }
            is Action.Succeed -> State.Reporting(oldState.nearestUsername, action.token)
            is Action.Fail -> State.Editing(
                partialUsername = oldState.nearestUsername,
                partialPassword = oldState.nearestPassword,
                error = action.throwable.localizedMessage,
                partialMfa = oldState.nearestMfa
            )
            is Action.Cancel -> {
                when (oldState) {
                    is State.Verifying -> State.Editing(
                        partialUsername = oldState.possibleUsername,
                        partialPassword = oldState.possiblePassword,
                        error = "",
                        partialMfa = oldState.possibleMfa
                    )
                    else -> State.Reporting(verifiedUsername = "", token = "")
                }
            }
        }
        setVision(state.toVision())
        afterUpdate?.invoke()
    }

    private fun startLogin(username: String, password: String, mfa: String) {
        loginDisposable = robinhoodApi.login(username, password, mfa)
            .map<Action>(Action::Succeed)
            .onErrorReturn(Action::Fail)
            .subscribeOn(Schedulers.io())
            .subscribe(this::sendAction)
    }

    private fun endLogin() {
        loginDisposable?.dispose()
    }

    private var loginDisposable: Disposable? = null

    private var state: State = State.Editing("", "", "", "")

    private sealed class State {
        abstract fun toVision(): Vision

        data class Editing(
            val partialUsername: String,
            val partialPassword: String,
            val error: String,
            val partialMfa: String
        ) : State() {

            override fun toVision(): Vision =
                Vision.Editing(partialUsername, partialPassword, error, isSubmittable, partialMfa)

            val isSubmittable: Boolean
                get() = partialUsername.isNotBlank() && partialPassword.isNotBlank() && partialPassword.length > 2
        }

        data class Verifying(
            val possibleUsername: String,
            val possiblePassword: String,
            val possibleMfa: String
        ) : State() {

            override fun toVision(): Vision = Vision.Submitting(possibleUsername, possiblePassword, possibleMfa)
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

        val nearestMfa: String
            get() = when (this) {
                is Editing -> partialMfa
                is Verifying -> possibleMfa
                else -> ""
            }
    }
}
