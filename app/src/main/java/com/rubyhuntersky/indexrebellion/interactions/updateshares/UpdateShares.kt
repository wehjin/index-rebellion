package com.rubyhuntersky.indexrebellion.interactions.updateshares

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.books.HoldingBook
import com.rubyhuntersky.interaction.core.SubjectInteraction
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.*
import com.rubyhuntersky.indexrebellion.interactions.updateshares.UpdateShares.Action as UpdateSharesAction

object UpdateShares {

    sealed class Vision {
        object Loading : Vision()

        data class Prompt(
            val assetSymbol: AssetSymbol,
            val ownedCount: Int,
            val sharePrice: PriceSample?,
            val newPrice: String?,
            val shouldUpdateCash: Boolean,
            val numberDelta: NumberDelta,
            val canUpdate: Boolean
        ) : Vision()

        object Dismissed : Vision()
    }

    sealed class Action {
        object Reset : UpdateSharesAction()
        data class Load(val ownedAsset: OwnedAsset) : UpdateSharesAction()
        data class ShouldUpdateCash(val shouldUpdateCash: Boolean) : UpdateSharesAction()
        data class NewPrice(val newSharePrice: String) : UpdateSharesAction()
        data class NewTotalCount(val newTotalCount: String) : UpdateSharesAction()
        data class NewChangeCount(val newChangeCount: String) : UpdateSharesAction()
        data class Save(val date: Date) : UpdateSharesAction()
    }


    class Interaction(private val holdingBook: HoldingBook) :
        SubjectInteraction<Vision, UpdateSharesAction>(
            startVision = UpdateShares.Vision.Loading,
            startAction = UpdateSharesAction.Reset
        ) {

        // TODO Delete these and build Prompt from Prompt
        private var ownedAsset: OwnedAsset? = null
        private var shouldUpdateCash = true
        private var newTotal = ""
        private var newChange = ""
        private var newPrice = ""

        private val composite = CompositeDisposable()

        override fun sendAction(action: UpdateSharesAction) {
            when (action) {
                is UpdateSharesAction.Reset -> {
                    setVision(UpdateShares.Vision.Loading)
                    composite.clear()
                    holdingBook.reader
                        .subscribe {
                            sendAction(UpdateSharesAction.Load(it))
                        }
                        .addTo(composite)
                }
                is UpdateSharesAction.Load -> startPrompt(action)
                is UpdateSharesAction.ShouldUpdateCash -> evolvePrompt { shouldUpdateCash = action.shouldUpdateCash }
                is UpdateSharesAction.NewPrice -> evolvePrompt { newPrice = action.newSharePrice }
                is UpdateSharesAction.NewTotalCount -> evolvePrompt { newTotal = action.newTotalCount }
                is UpdateSharesAction.NewChangeCount -> evolvePrompt { newChange = action.newChangeCount }
                is UpdateSharesAction.Save -> dismissPrompt(action.date)
            }
        }

        private fun startPrompt(action: UpdateSharesAction.Load) = when (vision) {
            is Vision.Loading, is Vision.Prompt -> {
                ownedAsset = action.ownedAsset
                shouldUpdateCash = true
                newTotal = ""
                newChange = ""
                newPrice = ""
                sendPromptVision()
            }
            is Vision.Dismissed -> Unit
        }

        private fun dismissPrompt(date: Date) = when (vision) {
            is Vision.Prompt -> {
                if (canUpdate) {
                    val newPriceDouble = newPrice.toDouble()
                    holdingBook.updateShareCountPriceAndCash(
                        assetSymbol = ownedAsset!!.assetSymbol,
                        shareCount = numberDelta.toShareCount(ownedAsset!!.shareCount),
                        sharePrice = PriceSample(CashAmount(newPriceDouble), date),
                        cashChange = if (shouldUpdateCash) {
                            val shareDelta = numberDelta.toShareDelta(ownedAsset!!.shareCount)
                            CashAmount(newPriceDouble * shareDelta.value * -1)
                        } else {
                            null
                        }
                    )
                }
                composite.clear()
                setVision(UpdateShares.Vision.Dismissed)
            }
            is Vision.Loading, is Vision.Dismissed -> Unit
        }

        private fun evolvePrompt(evolve: () -> Unit) = when (vision) {
            is Vision.Loading -> Unit
            is Vision.Prompt -> {
                evolve()
                sendPromptVision()
            }
            is Vision.Dismissed -> Unit
        }

        private fun sendPromptVision() = setVision(

            UpdateShares.Vision.Prompt(
                assetSymbol = ownedAsset!!.assetSymbol,
                ownedCount = ownedAsset!!.shareCount.toDouble().toInt(),
                sharePrice = ownedAsset!!.sharePrice,
                newPrice = newPrice,
                shouldUpdateCash = this.shouldUpdateCash,
                numberDelta = numberDelta,
                canUpdate = canUpdate
            )
        )

        private val numberDelta: NumberDelta
            get() = when {
                newTotal.isNotBlank() -> UpdateShares.NumberDelta.Total(
                    newTotal
                )
                newChange.isNotBlank() -> UpdateShares.NumberDelta.Change(
                    newChange
                )
                else -> UpdateShares.NumberDelta.Undecided
            }

        private val canUpdate: Boolean
            get() = newPrice.toDoubleOrNull() != null && numberDelta.isValid
    }

    sealed class NumberDelta {

        object Undecided : NumberDelta()

        data class Total(val newTotal: String) : NumberDelta()

        data class Change(val newChange: String) : NumberDelta()

        val isValid: Boolean
            get() = when (this) {
                is Undecided -> false
                is Total -> newTotal.toLongOrNull()?.let { it >= 0 } ?: false
                is Change -> newChange.toLongOrNull() != null
            }

        fun toShareCount(shareCount: ShareCount): ShareCount = when (this) {
            is Undecided -> shareCount
            is Total -> ShareCount(newTotal.toLong())
            is Change -> shareCount + ShareCount(newChange.toDouble())
        }

        fun toShareDelta(shareCount: ShareCount): ShareCount = when (this) {
            is Undecided -> ShareCount.ZERO
            is Total -> ShareCount(newTotal.toDouble()) - shareCount
            is Change -> ShareCount(newChange.toDouble())
        }
    }
}