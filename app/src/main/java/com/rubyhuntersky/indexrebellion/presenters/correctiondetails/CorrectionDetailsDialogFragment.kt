package com.rubyhuntersky.indexrebellion.presenters.correctiondetails

import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Action
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Vision
import com.rubyhuntersky.indexrebellion.vxandroid.InteractionBottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_correction_details.*


class CorrectionDetailsDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
    layoutRes = R.layout.view_correction_details,
    directInteraction = null
) {

    override val dismissAction: Action?
        get() = Action.Cancel

    override fun render(vision: Vision) {
        when (vision) {
            is Vision.Loading -> {
                symbolTextView.text = getString(R.string.loading)
                symbolTextView.text = null
                currentSharesTextView.text = null
                updateSharesTextView.isEnabled = false
                removeFromIndexTextView.isEnabled = false
            }
            is Vision.Viewing -> {
                val details = vision.details
                symbolTextView.text = details.assetSymbol.toString().toUpperCase()
                currentSharesTextView.text = getString(
                    R.string.n_shares,
                    details.ownedCount.toCountString(),
                    details.ownedValue.toStatString()
                )
                val delta = details.targetValue - details.ownedValue
                val deltaDirection = delta.compareTo(CashAmount.ZERO)
                notAdviceTextView.text = when {
                    deltaDirection > 0 -> getString(
                        R.string.buy_not_advice,
                        delta.toStatString(),
                        details.targetValue.toStatString()
                    )
                    deltaDirection < 0 -> getString(
                        R.string.sell_not_advice,
                        (-delta).toStatString(),
                        details.targetValue.toStatString()
                    )
                    else -> getString(R.string.hold_not_advice)
                }
                with(updateSharesTextView) {
                    isEnabled = true
                    setOnClickListener {
                        sendAction(Action.UpdateShares)
                        dismiss()
                    }
                }
                with(removeFromIndexTextView) {
                    isEnabled = true
                    setOnClickListener {
                        sendAction(Action.DeleteConstituent)
                        dismiss()
                    }
                }
            }
            is Vision.Finished -> dismiss()
        }
    }

    companion object {

        fun new(key: Long): CorrectionDetailsDialogFragment = CorrectionDetailsDialogFragment().also {
            it.indirectInteractionKey = key
        }
    }
}