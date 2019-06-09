package com.rubyhuntersky.indexrebellion.interactions.correctiondetails

import android.util.Log
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.rebellionBook
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.indexrebellion.interactions.updateshares.UpdateSharesStory
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.Story
import com.rubyhuntersky.interaction.core.wish.Lamp
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.indexrebellion.interactions.updateshares.Action as UpdateSharesAction
import com.rubyhuntersky.interaction.core.SubjectInteractionAdapter as Adapter

const val CORRECTION_DETAILS = "CorrectionDetails"

fun enableCorrectionDetails(lamp: Lamp) {
    with(lamp) {
        add(ReadCorrectionDetailsDjinn)
    }
}

class CorrectionDetailsStory : Interaction<Vision, Action> by Story(::start, ::isEnding, ::revise, CORRECTION_DETAILS)

sealed class Vision {
    data class Loading(val culture: Culture?) : Vision()
    data class Viewing(val details: CorrectionDetails, val culture: Culture) : Vision()
    object Finished : Vision()
}

data class Culture(val correctionDetailsBook: CorrectionDetailsBook)

fun start() = Vision.Loading(null)
fun isEnding(maybe: Any?) = maybe is Vision.Finished

sealed class Action {
    data class Start(val culture: Culture) : Action()
    data class Load(val correctionDetails: CorrectionDetails) : Action()
    object UpdateShares : Action()
    object DeleteConstituent : Action()
    object Cancel : Action()
}

fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val culture = action.culture
            val newVision = Vision.Loading(culture) as Vision
            val wish = ReadCorrectionDetailsDjinn.wish(
                name = "read",
                params = culture.correctionDetailsBook,
                resultToAction = { Action.Load(it) },
                errorToAction = { logError(it); Action.Cancel })
            Revision(newVision, wish)
        }
        action is Action.Cancel -> {
            if (vision is Vision.Finished) {
                Revision(vision as Vision)
            } else {
                val newVision = Vision.Finished as Vision
                val wish = Wish.none<Action>("read")
                Revision(newVision, wish)
            }
        }
        action is Action.Load -> {
            val culture = when (vision) {
                is Vision.Loading -> vision.culture ?: error("No culture")
                is Vision.Viewing -> vision.culture
                else -> throw NotImplementedError()
            }
            val newVision = Vision.Viewing(action.correctionDetails, culture)
            Revision(newVision)
        }
        vision is Vision.Viewing && action is Action.UpdateShares -> {
            val wish = edge.wish(
                name = "update-shares",
                interaction = UpdateSharesStory(),
                startAction = UpdateSharesAction.Start(rebellionBook, vision.details.assetSymbol),
                endVisionToAction = { Action.Cancel as Action }
            )
            Revision(Vision.Finished, wish)
        }
        vision is Vision.Viewing && action is Action.DeleteConstituent -> {
            vision.culture.correctionDetailsBook.deleteConstituent()
            Revision(Vision.Finished)
        }
        else -> throw NotImplementedError()
    }
}

private fun logError(error: Throwable) = Log.e(CORRECTION_DETAILS, error.localizedMessage, error)
