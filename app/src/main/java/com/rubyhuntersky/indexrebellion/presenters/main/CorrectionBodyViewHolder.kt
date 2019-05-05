package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.report.Correction
import com.rubyhuntersky.indexrebellion.data.toStatString
import kotlinx.android.synthetic.main.view_corrections_body.view.*

class CorrectionBodyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindCorrection(
        correction: Correction,
        correctionsHighWeight: Double,
        onCorrectionDetailsClick: (Correction) -> Unit
    ) {
        Log.d(this.javaClass.simpleName, "CORRECTION: $correction, HIGH WEIGHT: $correctionsHighWeight")
        with(itemView) {
            when (correction) {
                is Correction.Hold -> bindHold(correction, correctionsHighWeight)
                is Correction.Buy -> bindBuy(correction, correctionsHighWeight)
                is Correction.Sell -> bindSell(correction, correctionsHighWeight)
            }
            correctionActionButton.setOnClickListener { onCorrectionDetailsClick(correction) }
        }
    }

    private fun View.bindSell(correction: Correction.Sell, correctionsHighWeight: Double) {

        correctionHeadingTextView.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp)
        correctionActionButton.text = context.getString(
            R.string.sell_format,
            correction.surplus.toDouble().toStatString()
        )
        correctionActionButton.setIconResource(R.drawable.ic_remove_black_24dp)
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
        correctionActionButton.text = context.getString(
            R.string.buy_format,
            correction.deficit.toDouble().toStatString()
        )
        correctionActionButton.setIconResource(R.drawable.ic_add_black_24dp)
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
        correctionActionButton.setIconResource(R.drawable.ic_check_black_24dp)
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