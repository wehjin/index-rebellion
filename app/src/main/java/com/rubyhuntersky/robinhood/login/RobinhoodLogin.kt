package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access2
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.robinhood.api.RbhApi

class RobinhoodLoginInteraction : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, ROBINHOOD_LOGIN)

const val ROBINHOOD_LOGIN = "RobinhoodLogin"

sealed class Vision {

    abstract val username: String

    data class Editing(
        val rbhApi: RbhApi?,
        val deviceToken: String?,
        override val username: String,
        val password: String,
        val error: String,
        val mfa: String
    ) : Vision() {
        val submittable: Boolean
            get() = rbhApi != null && deviceToken != null
                    && username.isNotBlank() && password.isNotBlank() && password.length > 2
    }

    data class Submitting(
        val rbhApi: RbhApi,
        val deviceToken: String?,
        override val username: String,
        val password: String,
        val mfa: String
    ) : Vision()

    data class Reporting(override val username: String, val token: String) : Vision()
}

sealed class Action {
    data class Start(val rbhApi: RbhApi) : Action()
    data class Load(val access: Access2) : Action()
    data class SetUsername(val username: String) : Action()
    data class SetPassword(val password: String) : Action()
    data class SetMfa(val mfa: String) : Action()
    object Submit : Action()
    data class Succeed(val token: String) : Action()
    data class Fail(val throwable: Throwable) : Action()
    object Cancel : Action()
    data class Ignore(val ignore: Any) : Action()
}

fun start() = Vision.Editing(null, null, "", "", "", "") as Vision

fun isEnding(maybe: Any?) = maybe is Vision.Reporting

fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val deviceToken = when (vision) {
                is Vision.Editing -> vision.deviceToken
                is Vision.Submitting -> vision.deviceToken
                is Vision.Reporting -> null
            }
            val editing = Vision.Editing(
                rbhApi = action.rbhApi,
                deviceToken = deviceToken,
                username = vision.username,
                password = (vision as? Vision.Submitting)?.password ?: "",
                error = "",
                mfa = (vision as? Vision.Submitting)?.mfa ?: ""
            )
            if (deviceToken == null) {
                val readAccess = ReadAccess.toWish2("read-access", Action::Load, Action::Ignore)
                Revision(editing, Wish.none(SUBMISSION_WISH), readAccess)
            } else {
                Revision(editing, Wish.none<Action>(SUBMISSION_WISH))
            }
        }
        vision is Vision.Editing && action is Action.Load -> Revision(
            vision.copy(username = action.access.username, deviceToken = action.access.rbhDeviceToken)
        )
        vision is Vision.Editing && action is Action.SetUsername -> Revision(vision.copy(username = action.username))
        vision is Vision.Editing && action is Action.SetPassword -> Revision(vision.copy(password = action.password))
        vision is Vision.Editing && action is Action.SetMfa -> Revision(vision.copy(mfa = action.mfa))
        vision is Vision.Editing && action is Action.Submit -> {
            if (!vision.submittable) Revision(vision)
            else {
                val rbhApi = vision.rbhApi!!
                val fetchToken = FetchRbhAccessTokenGenie.wish(
                    name = SUBMISSION_WISH,
                    params = FetchRbhAccessToken(
                        rbhApi,
                        vision.deviceToken!!,
                        vision.username,
                        vision.password,
                        vision.mfa
                    ),
                    resultToAction = Action::Succeed,
                    errorToAction = Action::Fail
                )
                val submitting =
                    Vision.Submitting(rbhApi, vision.deviceToken, vision.username, vision.password, vision.mfa)
                Revision(submitting, fetchToken)
            }
        }
        vision is Vision.Submitting && action is Action.Fail -> {
            val error = action.throwable.localizedMessage
            val editing =
                Vision.Editing(vision.rbhApi, vision.deviceToken, vision.username, vision.password, error, vision.mfa)
            Revision(editing)
        }
        vision is Vision.Submitting && action is Action.Cancel -> {
            val editing = Vision.Editing(vision.rbhApi, vision.deviceToken, vision.username, vision.password, "", "")
            Revision(editing, Wish.none(SUBMISSION_WISH))
        }
        vision is Vision.Submitting && action is Action.Succeed -> {
            val writeAccess = WriteAccess(Access2(vision.username, action.token))
                .toWish2(
                    name = "save-token",
                    onResult = Action::Ignore,
                    onError = Action::Ignore
                )
            val reporting = Vision.Reporting(vision.username, action.token)
            Revision(reporting, writeAccess)
        }
        action is Action.Cancel -> {
            val reporting = Vision.Reporting(vision.username, "")
            Revision(reporting, Wish.none(SUBMISSION_WISH))
        }
        else -> Revision<Vision, Action>(vision)
            .also { System.err.println(addTag("BAD REVISION: $action, $vision")) }
    }
}

private fun addTag(message: String): String = "$ROBINHOOD_LOGIN $message"

private const val SUBMISSION_WISH = "submission"
