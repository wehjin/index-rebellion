package com.rubyhuntersky.indexrebellion.presenters.updateshares

import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.SharePrice
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionConstituentBook
import com.rubyhuntersky.indexrebellion.interactions.updateshares.UpdateShares
import com.rubyhuntersky.indexrebellion.vxandroid.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.core.InteractionRegistry
import com.rubyhuntersky.interaction.core.Portal
import kotlinx.android.synthetic.main.view_update_share_count.*
import java.util.*
import kotlin.random.Random


class UpdateSharesDialogFragment : InteractionBottomSheetDialogFragment<UpdateShares.Vision, UpdateShares.Action>(
    layoutRes = R.layout.view_update_share_count,
    directInteraction = null
) {


    override fun render(vision: UpdateShares.Vision) {
        when (vision) {
            is UpdateShares.Vision.Loading -> {
                symbolTextView.text = getString(R.string.loading)
            }
            is UpdateShares.Vision.Prompt -> {
                symbolTextView.text = vision.assetSymbol.string
                renderCountViews(vision.numberDelta, vision.ownedCount)
                renderPriceViews(vision.sharePrice, vision.newPrice)
                renderCashAdjustmentCheckBox(vision.shouldUpdateCash)
                renderSaveButton(vision.canUpdate)
            }
            is UpdateShares.Vision.Dismissed -> {
                symbolTextView.text = getString(R.string.dismissed)
                dismiss()
            }
        }
    }

    private fun renderSaveButton(canUpdate: Boolean) {
        saveButton.isEnabled = canUpdate
        if (canUpdate) {
            saveButton.setOnClickListener { sendAction(UpdateShares.Action.Save(Date())) }
        }
    }

    private fun renderCashAdjustmentCheckBox(shouldUpdateCash: Boolean) {
        adjustCashCheckBox.setOnCheckedChangeListener(null)
        if (adjustCashCheckBox.isChecked != shouldUpdateCash) {
            adjustCashCheckBox.isChecked = shouldUpdateCash
        }
        adjustCashCheckBox.setOnCheckedChangeListener { _, isChecked ->
            sendAction(UpdateShares.Action.ShouldUpdateCash(isChecked))
        }
    }

    private fun renderPriceViews(oldPrice: SharePrice, newPrice: String?) {
        sharePriceEditText.removeTextChangedListener(sharePriceTextWatcher)
        sharePriceEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                sharePriceEditText.hint = oldPrice?.let { oldPrice.cashAmount.toDouble().toString() }
            } else {
                sharePriceEditText.hint = null
            }
        }
        if (sharePriceEditText.text.toString() != newPrice) {
            sharePriceEditText.setText(newPrice)
        }
        sharePriceEditText.addTextChangedListener(sharePriceTextWatcher)
    }

    private fun renderCountViews(numberDelta: UpdateShares.NumberDelta, ownedCount: Int) {
        oldCountTextView.text = getString(R.string.old_shares_format, ownedCount.toString())
        when (numberDelta) {
            is UpdateShares.NumberDelta.Undecided -> {
                totalCountEditText.removeTextChangedListener(totalTextWatcher)
                totalCountEditText.isEnabled = true
                totalCountEditText.text = null
                totalCountEditText.addTextChangedListener(totalTextWatcher)
                deltaCountEditText.removeTextChangedListener(deltaTextWatcher)
                deltaCountEditText.isEnabled = true
                deltaCountEditText.text = null
                deltaCountEditText.addTextChangedListener(deltaTextWatcher)
            }
            is UpdateShares.NumberDelta.Total -> {
                totalCountEditText.removeTextChangedListener(totalTextWatcher)
                totalCountEditText.isEnabled = true
                if (totalCountEditText.text.toString() != numberDelta.newTotal) {
                    totalCountEditText.setText(numberDelta.newTotal)
                }
                totalCountEditText.addTextChangedListener(totalTextWatcher)
                deltaCountEditText.removeTextChangedListener(deltaTextWatcher)
                deltaCountEditText.isEnabled = false
                val delta = numberDelta.newTotal.toLongOrNull()?.let { it - ownedCount }
                deltaCountEditText.setText(delta?.toString() ?: getString(R.string.unknown_quantity))
            }
            is UpdateShares.NumberDelta.Change -> {
                totalCountEditText.removeTextChangedListener(totalTextWatcher)
                totalCountEditText.isEnabled = false
                val total = numberDelta.newChange.toLongOrNull()?.let { it + ownedCount }
                totalCountEditText.setText(total?.toString() ?: getString(R.string.unknown_quantity))
                deltaCountEditText.removeTextChangedListener(deltaTextWatcher)
                deltaCountEditText.isEnabled = true
                if (deltaCountEditText.text.toString() != numberDelta.newChange) {
                    deltaCountEditText.setText(numberDelta.newChange)
                }
                deltaCountEditText.addTextChangedListener(deltaTextWatcher)
            }
        }
    }

    private val totalTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let { sendAction(UpdateShares.Action.NewTotalCount(it.toString())) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private val deltaTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let { sendAction(UpdateShares.Action.NewChangeCount(it.toString())) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private val sharePriceTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let { sendAction(UpdateShares.Action.NewPrice(it.toString())) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    companion object : Portal<Pair<AssetSymbol, () -> FragmentActivity>> {

        override fun jump(carry: Pair<AssetSymbol, () -> FragmentActivity>) {
            Log.d(this::class.java.simpleName, "Catalyzing UpdateShares: ${carry.first}")
            val key = Random.nextLong()
            val fragment = UpdateSharesDialogFragment()
                .also {
                    val constituentBook = RebellionConstituentBook(SharedRebellionBook, carry.first)
                    val interaction = UpdateShares.Interaction(constituentBook)
                        .apply {
                            reset()
                        }
                    it.indirectInteractionKey = key
                    InteractionRegistry.addInteraction(it.indirectInteractionKey, interaction)
                }
            fragment.show(carry.second().supportFragmentManager, key.toString())
        }
    }
}

