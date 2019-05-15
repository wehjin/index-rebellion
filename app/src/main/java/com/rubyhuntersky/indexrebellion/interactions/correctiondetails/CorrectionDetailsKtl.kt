package com.rubyhuntersky.indexrebellion.interactions.correctiondetails

import android.util.Log
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.SubjectInteractionAdapter as Adapter

const val CORRECTION_DETAILS = "CorrectionDetails"

class CorrectionDetailsStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, CORRECTION_DETAILS)

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

fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val culture = action.culture
            val newVision = Vision.Loading(culture) as Vision
            val wish = culture.correctionDetailsBook.reader.toWish("read", Action::Load, ::logError) as Wish<Action>
            Revision(newVision, wish)
        }
        action is Action.Cancel -> {
            if (vision is Vision.Finished) {
                Revision(vision as Vision)
            } else {
                val newVision = Vision.Finished as Vision
                val wish = Wish.None("read") as Wish<Action>
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
            val assetSymbol = vision.details.assetSymbol
            //vision.culture.updateSharesPortal.jump(assetSymbol)
            Revision(Vision.Finished)
        }
        vision is Vision.Viewing && action is Action.DeleteConstituent -> {
            vision.culture.correctionDetailsBook.deleteConstituent()
            Revision(Vision.Finished)
        }
        else -> throw NotImplementedError()
    }
}

private fun logError(error: Throwable) = Log.e(CORRECTION_DETAILS, error.localizedMessage, error)
