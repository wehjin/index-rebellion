package com.rubyhuntersky.indexrebellion.presenters.updateshares

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.interactions.updateshares.*
import com.rubyhuntersky.indexrebellion.vxandroid.InteractionBottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_update_share_count.*
import java.util.*


class UpdateSharesDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
    layoutRes = R.layout.view_update_share_count,
    directInteraction = null
) {

    override fun render(vision: Vision) {
        Log.d(UPDATE_SHARES, "VISION: $vision")
        when (vision) {
            is Vision.Loading -> {
                symbolTextView.text = getString(R.string.loading)
            }
            is Vision.Prompt -> {
                symbolTextView.text = vision.assetSymbol.string
                renderCountViews(vision.sharesChange, vision.ownedCount)
                renderPriceViews(vision.sharePrice, vision.newPrice)
                renderCashAdjustmentCheckBox(vision.shouldUpdateCash)
                renderSaveButton(vision.canUpdate)
            }
            is Vision.Dismissed -> {
                symbolTextView.text = getString(R.string.dismissed)
                dismiss()
            }
        }
    }

    private fun renderSaveButton(canUpdate: Boolean) {
        saveButton.isEnabled = canUpdate
        if (canUpdate) {
            saveButton.setOnClickListener { sendAction(Action.Save(Date())) }
        }
    }

    private fun renderCashAdjustmentCheckBox(shouldUpdateCash: Boolean) {
        adjustCashCheckBox.setOnCheckedChangeListener(null)
        if (adjustCashCheckBox.isChecked != shouldUpdateCash) {
            adjustCashCheckBox.isChecked = shouldUpdateCash
        }
        adjustCashCheckBox.setOnCheckedChangeListener { _, isChecked ->
            sendAction(Action.ShouldUpdateCash(isChecked))
        }
    }

    private fun renderPriceViews(oldPrice: PriceSample?, newPrice: String?) {
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

    private fun renderCountViews(sharesChange: SharesChange, ownedCount: ShareCount) {
        oldCountTextView.text = getString(R.string.old_shares_format, ownedCount.toDouble().toLong().toString())
        when (sharesChange) {
            is SharesChange.None -> {
                totalCountEditText.removeTextChangedListener(totalTextWatcher)
                totalCountEditText.isEnabled = true
                totalCountEditText.text = null
                totalCountEditText.addTextChangedListener(totalTextWatcher)
                deltaCountEditText.removeTextChangedListener(deltaTextWatcher)
                deltaCountEditText.isEnabled = true
                deltaCountEditText.text = null
                deltaCountEditText.addTextChangedListener(deltaTextWatcher)
            }
            is SharesChange.Total -> {
                totalCountEditText.removeTextChangedListener(totalTextWatcher)
                totalCountEditText.isEnabled = true
                if (totalCountEditText.text.toString() != sharesChange.total) {
                    totalCountEditText.setText(sharesChange.total)
                }
                totalCountEditText.addTextChangedListener(totalTextWatcher)
                deltaCountEditText.removeTextChangedListener(deltaTextWatcher)
                deltaCountEditText.isEnabled = false
                val delta = sharesChange.total.toLongOrNull()?.let { it - ownedCount.toDouble().toLong() }
                deltaCountEditText.setText(delta?.toString() ?: getString(R.string.unknown_quantity))
            }
            is SharesChange.Addition -> {
                totalCountEditText.removeTextChangedListener(totalTextWatcher)
                totalCountEditText.isEnabled = false
                val total = sharesChange.addition.toLongOrNull()?.let { it + ownedCount.toDouble().toLong() }
                totalCountEditText.setText(total?.toString() ?: getString(R.string.unknown_quantity))
                deltaCountEditText.removeTextChangedListener(deltaTextWatcher)
                deltaCountEditText.isEnabled = true
                if (deltaCountEditText.text.toString() != sharesChange.addition) {
                    deltaCountEditText.setText(sharesChange.addition)
                }
                deltaCountEditText.addTextChangedListener(deltaTextWatcher)
            }
        }
    }

    private val totalTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val newSharesChange = Action.NewSharesChange(SharesChange.Total(it.toString()))
                sendAction(newSharesChange)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private val deltaTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val newSharesChange = Action.NewSharesChange(SharesChange.Addition(it.toString()))
                sendAction(newSharesChange)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    private val sharePriceTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let { sendAction(Action.NewPrice(it.toString())) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    companion object {

        fun new(key: Long): UpdateSharesDialogFragment {
            return UpdateSharesDialogFragment().also {
                it.indirectInteractionKey = key
            }
        }
    }
}

