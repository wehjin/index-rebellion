package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.wish.Lamp
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.robinhood.api.RbhApi

class RobinhoodLoginInteraction : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, ROBINHOOD_LOGIN)

const val ROBINHOOD_LOGIN = "RobinhoodLogin"

fun enableRobinhoodLogin(lamp: Lamp) {
    with(lamp) {
        add(FetchRbhAccessTokenGenie)
        add(SaveAccessTokenGenie)
    }
}

sealed class Vision {

    abstract val username: String

    data class Editing(
        val services: Services?,
        override val username: String,
        val password: String,
        val error: String,
        val mfa: String
    ) : Vision() {
        val submittable: Boolean
            get() = services != null
                    && username.isNotBlank()
                    && password.isNotBlank()
                    && password.length > 2
    }

    data class Submitting(
        val services: Services,
        override val username: String,
        val password: String,
        val mfa: String
    ) : Vision()

    data class Reporting(override val username: String, val token: String) : Vision()
}

data class Services(val rbhApi: RbhApi, val accessBook: Book<Access>)

fun start() = Vision.Editing(null, "", "", "", "") as Vision
fun isEnding(maybe: Any?) = maybe is Vision.Reporting

sealed class Action {
    data class Start(val services: Services) : Action()
    data class SetUsername(val username: String) : Action()
    data class SetPassword(val password: String) : Action()
    data class SetMfa(val mfa: String) : Action()
    object Submit : Action()
    data class Succeed(val token: String) : Action()
    data class Fail(val throwable: Throwable) : Action()
    object Cancel : Action()
    object Ignore : Action()
}

fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val password = (vision as? Vision.Submitting)?.password ?: ""
            val username = action.services.accessBook.value.username
            val mfa = (vision as? Vision.Submitting)?.mfa ?: ""
            val endSubmission = Wish.none<Action>(SUBMISSION_WISH)
            val editing = Vision.Editing(action.services, username, password, "", mfa)
            Revision(editing, endSubmission)
        }
        vision is Vision.Editing && action is Action.SetUsername -> {
            val newVision = Vision.Editing(vision.services, action.username, vision.password, vision.error, vision.mfa)
            Revision(newVision)
        }
        vision is Vision.Editing && action is Action.SetPassword -> {
            val newVision = Vision.Editing(vision.services, vision.username, action.password, vision.error, vision.mfa)
            Revision(newVision)
        }
        vision is Vision.Editing && action is Action.SetMfa -> {
            val editing =
                Vision.Editing(vision.services, vision.username, vision.password, vision.error, action.mfa)
            Revision(editing)
        }
        vision is Vision.Editing && action is Action.Submit -> {
            if (vision.submittable) {
                val services = vision.services!!
                val accessTokenWish = FetchRbhAccessTokenGenie.wish(
                    name = SUBMISSION_WISH,
                    params = FetchRbhAccessToken(services.rbhApi, vision.username, vision.password, vision.mfa),
                    resultToAction = Action::Succeed,
                    errorToAction = Action::Fail
                )
                val submitting = Vision.Submitting(services, vision.username, vision.password, vision.mfa) as Vision
                Revision(submitting, accessTokenWish)
            } else {
                Revision(vision as Vision)
            }
        }
        vision is Vision.Submitting && action is Action.Succeed -> {
            val reporting = Vision.Reporting(vision.username, action.token)
            val saveTokenWish = SaveAccessTokenGenie.wish(
                name = "save-token",
                params = SaveAccessToken(vision.services.accessBook, action.token),
                resultToAction = { Action.Ignore },
                errorToAction = { Action.Ignore }
            )
            Revision(reporting, saveTokenWish)
        }
        vision is Vision.Submitting && action is Action.Fail -> {
            val error = action.throwable.localizedMessage
            val newVision = Vision.Editing(vision.services, vision.username, vision.password, error, vision.mfa)
            Revision(newVision)
        }
        vision is Vision.Submitting && action is Action.Cancel -> {
            val newVision = Vision.Editing(vision.services, vision.username, vision.password, "", "")
            val cancelSubmission = Wish.none<Action>(SUBMISSION_WISH)
            Revision(newVision, cancelSubmission)
        }
        action is Action.Cancel -> {
            val reporting = Vision.Reporting(vision.username, "")
            val cancelSubmission = Wish.none<Action>(SUBMISSION_WISH)
            Revision(reporting, cancelSubmission)
        }
        else -> throw NotImplementedError()
    }
}

private const val SUBMISSION_WISH = "submission"
