package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.robinhood.api.RbhApi
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class RobinhoodLoginInteraction(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, ROBINHOOD_LOGIN)

const val ROBINHOOD_LOGIN = "RobinhoodLogin"

sealed class Vision {

    abstract val username: String

    data class Editing(
        val services: Services?,
        override val username: String,
        val password: String,
        val error: String,
        val submittable: Boolean,
        val mfa: String
    ) : Vision()

    data class Submitting(
        val services: Services,
        override val username: String,
        val password: String,
        val mfa: String
    ) : Vision()

    data class Reporting(override val username: String, val token: String) : Vision()
}

data class Services(val rbhApi: RbhApi, val accessBook: Book<Access>)

fun start() = Vision.Editing(null, "", "", "", false, "") as Vision
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
            val submittable = isSubmittable(username, password)
            val endSubmission = Wish.None(SUBMISSION_WISH) as Wish<Action>
            val editing = Vision.Editing(action.services, username, password, "", submittable, mfa)
            Revision(editing, endSubmission)
        }
        vision is Vision.Editing && action is Action.SetUsername -> {
            val submittable = isSubmittable(action.username, vision.password)
            val editing =
                Vision.Editing(vision.services, action.username, vision.password, vision.error, submittable, vision.mfa)
            Revision(editing)
        }
        vision is Vision.Editing && action is Action.SetPassword -> {
            val submittable = isSubmittable(vision.username, action.password)
            val editing =
                Vision.Editing(vision.services, vision.username, action.password, vision.error, submittable, vision.mfa)
            Revision(editing)
        }
        vision is Vision.Editing && action is Action.SetMfa -> {
            val submittable = isSubmittable(vision.username, vision.password)
            val editing =
                Vision.Editing(vision.services, vision.username, vision.password, vision.error, submittable, action.mfa)
            Revision(editing)
        }
        vision is Vision.Editing && action is Action.Submit -> {
            if (vision.submittable) {
                val submissionWish = vision.services
                    ?.rbhApi?.login(vision.username, vision.password, vision.mfa)
                    ?.subscribeOn(Schedulers.io())
                    ?.toWish(SUBMISSION_WISH, Action::Succeed, Action::Fail) as Wish<Action>
                val submitting =
                    Vision.Submitting(vision.services, vision.username, vision.password, vision.mfa) as Vision
                Revision(submitting, submissionWish)
            } else {
                Revision(vision as Vision)
            }
        }
        vision is Vision.Submitting && action is Action.Succeed -> {
            val reporting = Vision.Reporting(vision.username, action.token)
            val token = action.token
            val accessBook = vision.services.accessBook
            val saveTokenWish = saveToken(accessBook, token)
                .toSingleDefault(Unit)
                .toWish("save-token", { Action.Ignore as Action }, { Action.Ignore })
            Revision(reporting, saveTokenWish)
        }
        vision is Vision.Submitting && action is Action.Fail -> {
            val submittable = isSubmittable(vision.username, vision.password)
            val error = action.throwable.localizedMessage
            val editing =
                Vision.Editing(vision.services, vision.username, vision.password, error, submittable, vision.mfa)
            Revision(editing)
        }
        vision is Vision.Submitting && action is Action.Cancel -> {
            val submittable = isSubmittable(vision.username, vision.password)
            val editing = Vision.Editing(vision.services, vision.username, vision.password, "", submittable, "")
            val cancelSubmission = Wish.None(SUBMISSION_WISH) as Wish<Action>
            Revision(editing, cancelSubmission)
        }
        action is Action.Cancel -> {
            val reporting = Vision.Reporting(vision.username, "")
            val cancelSubmission = Wish.None(SUBMISSION_WISH) as Wish<Action>
            Revision(reporting, cancelSubmission)
        }
        else -> throw NotImplementedError()
    }
}

private fun saveToken(accessBook: Book<Access>, token: String) = Completable.create {
    val newAccess = accessBook.value.withToken(token)
    accessBook.write(newAccess)
}

private const val SUBMISSION_WISH = "submission"

private fun isSubmittable(username: String, password: String): Boolean {
    return username.isNotBlank() && password.isNotBlank() && password.length > 2
}
