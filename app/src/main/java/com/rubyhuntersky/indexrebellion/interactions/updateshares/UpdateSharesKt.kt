package com.rubyhuntersky.indexrebellion.interactions.updateshares

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.interaction.core.*
import java.util.*

const val UPDATE_SHARES = "UpdateShares"

class UpdateSharesStory(well: Well) : Interaction<Vision, Action>
by Story(well, ::start, ::isEnding, ::revise, UPDATE_SHARES)

sealed class Vision {
    object Loading : Vision()

    data class Prompt(
        val assetSymbol: AssetSymbol,
        val ownedCount: ShareCount,
        val sharesChange: SharesChange,
        val sharePrice: PriceSample?,
        val newPrice: String?,
        val shouldUpdateCash: Boolean,
        val rebellionBook: Book<Rebellion>
    ) : Vision()

    object Dismissed : Vision()
}

sealed class SharesChange {

    object None : SharesChange()

    data class Total(val total: String) : SharesChange()

    data class Addition(val addition: String) : SharesChange()

    val isValid: Boolean
        get() = when (this) {
            is None -> false
            is Total -> total.toLongOrNull()?.let { it >= 0 } ?: false
            is Addition -> addition.toLongOrNull() != null
        }

    val isChanged: Boolean
        get() = this is Total || this is Addition

    fun toShareCount(shareCount: ShareCount): ShareCount = when (this) {
        is None -> shareCount
        is Total -> ShareCount(total.toLong())
        is Addition -> shareCount + ShareCount(addition.toDouble())
    }

    fun toShareDelta(shareCount: ShareCount): ShareCount = when (this) {
        is None -> ShareCount.ZERO
        is Total -> ShareCount(total.toDouble()) - shareCount
        is Addition -> ShareCount(addition.toDouble())
    }
}

private fun start() = Vision.Loading
private fun isEnding(maybe: Any?) = maybe is Vision.Dismissed

sealed class Action {
    data class Start(val rebellionBook: Book<Rebellion>, val assetSymbol: AssetSymbol) : Action()
    data class ShouldUpdateCash(val shouldUpdateCash: Boolean) : Action()
    data class NewPrice(val newPrice: String) : Action()
    data class NewSharesChange(val newSharesChange: SharesChange) : Action()
    data class Save(val date: Date) : Action()
}

private fun revise(vision: Vision, action: Action): Revision<Vision, Action> {
    return when {
        action is Action.Start -> {
            val ownedAsset = action.rebellionBook.value.findHolding(action.assetSymbol)
            val newVision = Vision.Prompt(
                assetSymbol = action.assetSymbol,
                ownedCount = ownedAsset?.shareCount ?: ShareCount.ZERO,
                sharesChange = SharesChange.None,
                sharePrice = ownedAsset?.sharePrice,
                newPrice = null,
                shouldUpdateCash = true,
                rebellionBook = action.rebellionBook
            )
            Revision(newVision)
        }
        vision is Vision.Prompt && action is Action.ShouldUpdateCash -> {
            val newVision = Vision.Prompt(
                vision.assetSymbol,
                vision.ownedCount,
                vision.sharesChange,
                vision.sharePrice,
                vision.newPrice,
                action.shouldUpdateCash,
                vision.rebellionBook
            )
            Revision(newVision)
        }
        vision is Vision.Prompt && action is Action.NewPrice -> {
            val newVision = Vision.Prompt(
                vision.assetSymbol,
                vision.ownedCount,
                vision.sharesChange,
                vision.sharePrice,
                action.newPrice,
                vision.shouldUpdateCash,
                vision.rebellionBook
            )
            Revision(newVision)
        }
        vision is Vision.Prompt && action is Action.NewSharesChange -> {
            val newVision = Vision.Prompt(
                vision.assetSymbol,
                vision.ownedCount,
                action.newSharesChange,
                vision.sharePrice,
                vision.newPrice,
                vision.shouldUpdateCash,
                vision.rebellionBook
            )
            Revision(newVision)
        }
        vision is Vision.Prompt && action is Action.Save -> {
            if (vision.canUpdate) {
                val assetSymbol = vision.assetSymbol
                val shareCount = vision.sharesChange.toShareCount(vision.ownedCount)
                val sharePrice = if (vision.newPrice.isNullOrBlank()) {
                    vision.sharePrice
                } else {
                    PriceSample(CashAmount(vision.newPrice.toDoubleOrNull() ?: 0.0), action.date)
                }
                val unspentInvestmentChange = if (vision.shouldUpdateCash) {
                    val shareDelta = vision.sharesChange.toShareDelta(vision.ownedCount)
                    val sharePrice = sharePrice?.toDouble() ?: 0.0
                    CashAmount(sharePrice * shareDelta.value * -1)
                } else {
                    CashAmount.ZERO
                }
                val newRebellion = vision.rebellionBook.value.withShareCountPriceAndUnspentInvestment(
                    assetSymbol,
                    shareCount,
                    sharePrice,
                    unspentInvestmentChange
                )
                vision.rebellionBook.write(newRebellion)
                Revision(Vision.Dismissed as Vision)
            } else {
                Revision(vision as Vision)
            }
        }
        else -> throw NotImplementedError()
    }
}

val Vision.Prompt.canUpdate: Boolean
    get() {
        val newPriceIsValid = newPrice == null || newPrice.toDoubleOrNull() != null
        val sharesChangeIsValid = sharesChange.isChanged && sharesChange.isValid
        return newPriceIsValid || sharesChangeIsValid
    }
