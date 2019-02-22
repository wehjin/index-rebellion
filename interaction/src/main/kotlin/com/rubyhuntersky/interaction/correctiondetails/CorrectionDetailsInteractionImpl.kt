package com.rubyhuntersky.interaction.correctiondetails

import com.rubyhuntersky.data.assets.AssetSymbol
import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.books.CorrectionDetailsBook
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.Saver
import com.rubyhuntersky.interaction.core.BehaviorInteractionAdapter as Adapter

typealias CorrectionDetailsInteraction = Interaction<Vision, Action>

sealed class Vision {
    object Loading : Vision()
    data class Viewing(val details: CorrectionDetails) : Vision()
    object Finished : Vision()
}

sealed class Action {
    object UpdateShares : Action()
    object Delete : Action()
}

class CorrectionDetailsInteractionImpl(
    correctionDetailsBook: CorrectionDetailsBook,
    updateSharesPortal: Portal<AssetSymbol>
) : CorrectionDetailsInteraction
by Saver.InteractionImpl(correctionDetailsBook)
    .adapt(object : Adapter<Saver.Vision<CorrectionDetails>, Saver.Action<CorrectionDetails>, Vision, Action> {
        override fun onVision(
            vision: Saver.Vision<CorrectionDetails>,
            controller: Adapter.Controller<Vision, Saver.Action<CorrectionDetails>>
        ) =
            when (vision) {
                is Saver.Vision.Reading -> controller.setVision(Vision.Loading)
                is Saver.Vision.Ready -> controller.setVision(Vision.Viewing(vision.value))
            }

        override fun onAction(
            action: Action,
            controller: Adapter.Controller<Vision, Saver.Action<CorrectionDetails>>
        ) {
            when (action) {
                is Action.UpdateShares -> (controller.vision as? Vision.Viewing)?.let {
                    updateSharesPortal.jump(it.details.assetSymbol)
                    controller.setVision(Vision.Finished)
                }
                is Action.Delete -> (controller.vision as? Vision.Viewing)?.let {
                    correctionDetailsBook.delete()
                    controller.setVision(Vision.Finished)
                }
            }
        }
    })
