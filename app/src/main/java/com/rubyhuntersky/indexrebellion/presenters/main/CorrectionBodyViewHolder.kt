package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.indexrebellion.R
import kotlinx.android.synthetic.main.view_corrections_body.view.*

class CorrectionBodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCorrection(correction: Correction, correctionsHighWeight: Double) {
        with(itemView) {
            when (correction) {
                is Correction.Hold -> bindHold(correction, correctionsHighWeight)
                is Correction.Buy -> bindBuy(correction, correctionsHighWeight)
                is Correction.Sell -> bindSell(correction, correctionsHighWeight)
            }
        }
    }

    private fun View.bindSell(correction: Correction.Sell, correctionsHighWeight: Double) {

        correctionHeadingTextView.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp)
        correctionActionButton.text = context.getString(R.string.sell)
        rightSpecial.setBackgroundResource(R.drawable.bg_outlined_rectangle)
        setCorrectionWeights(
            CorrectionWeightsCalculator.calculate(
                highValue = correctionsHighWeight,
                targetValue = correction.targetWeight,
                actualValue = correction.actualWeight
            )
        )
    }

    private fun View.bindBuy(correction: Correction.Buy, correctionsHighWeight: Double) {

        correctionHeadingTextView.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_add_circle_black_24dp)
        correctionActionButton.text = context.getString(R.string.buy)
        rightSpecial.setBackgroundResource(R.color.secondaryColor)
        setCorrectionWeights(
            CorrectionWeightsCalculator.calculate(
                highValue = correctionsHighWeight,
                targetValue = correction.targetWeight,
                actualValue = correction.actualWeight
            )
        )
    }

    private fun View.bindHold(correction: Correction.Hold, correctionsHighWeight: Double) {

        correctionHeadingTextView.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_check_circle_black_24dp)
        correctionActionButton.text = context.getString(R.string.hold)
        setCorrectionWeights(
            CorrectionWeightsCalculator.calculate(
                highValue = correctionsHighWeight,
                targetValue = correction.weight,
                actualValue = correction.weight
            )
        )
    }

    private fun View.setCorrectionWeights(correctionWeights: CorrectionWeights) {
        leftSpace.setWeight(correctionWeights.leftSpace.toFloat())
        leftWing.setWeight(correctionWeights.leftWing.toFloat())
        rightWing.setWeight(correctionWeights.rightWing.toFloat())
        rightSpecial.setWeight(correctionWeights.rightSpecial.toFloat())
        rightSpace.setWeight(correctionWeights.rightSpace.toFloat())
    }

    private fun View.setWeight(weight: Float) {
        val params = layoutParams as LinearLayout.LayoutParams
        params.weight = weight
        layoutParams = params
    }
}