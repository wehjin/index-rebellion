package com.rubyhuntersky.indexrebellion.presenters.main

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.report.Correction
import com.rubyhuntersky.indexrebellion.data.toStatString
import kotlinx.android.synthetic.main.view_corrections_body.view.*
import kotlin.math.abs

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
            setOnClickListener { onCorrectionDetailsClick(correction) }
        }
    }

    private fun View.bindSell(correction: Correction.Sell, correctionsHighWeight: Double) {

        correctionLabel.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp)
        correctionActionValue.text =
            context.getString(R.string.sell_format, correction.surplus.toDouble().toStatString())
        correctionActionVerb.text = context.getString(R.string.divest)
        rightSpecial.setBackgroundResource(R.drawable.bg_outlined_rectangle)
        setCorrectionWeights(
            CorrectionWeightsCalculator.calculate(
                highValue = correctionsHighWeight,
                targetValue = correction.targetWeight,
                actualValue = correction.actualWeight
            )
        )
        setTilt(correction.actualWeight, correction.targetWeight)
    }

    private fun View.bindBuy(correction: Correction.Buy, correctionsHighWeight: Double) {

        correctionLabel.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_add_circle_black_24dp)
        correctionActionValue.text = context.getString(
            R.string.buy_format,
            correction.deficit.toDouble().toStatString()
        )
        correctionActionVerb.text = context.getString(R.string.invest)
        rightSpecial.setBackgroundResource(R.color.secondaryColor)
        setCorrectionWeights(
            CorrectionWeightsCalculator.calculate(
                highValue = correctionsHighWeight,
                targetValue = correction.targetWeight,
                actualValue = correction.actualWeight
            )
        )
        setTilt(correction.actualWeight, correction.targetWeight)
    }

    private fun View.bindHold(correction: Correction.Hold, correctionsHighWeight: Double) {

        correctionLabel.text = correction.assetSymbol.toString()
        correctionCircleView.setBackgroundResource(R.drawable.ic_check_circle_black_24dp)
        correctionActionValue.text = context.getString(R.string.hold_value)
        correctionActionVerb.text = context.getString(R.string.hold)
        setCorrectionWeights(
            CorrectionWeightsCalculator.calculate(
                highValue = correctionsHighWeight,
                targetValue = correction.weight,
                actualValue = correction.weight
            )
        )
        setTilt(correction.weight, correction.weight)
    }

    private fun View.setTilt(actualWeight: Double, targetWeight: Double) {
        val tilt = 0.15f
        val balancedScale = 1.0f - tilt
        when {
            actualWeight == targetWeight -> {
                leftWing.scaleY = balancedScale
                rightWing.scaleY = balancedScale
                rightSpecial.scaleY = balancedScale
            }
            actualWeight > targetWeight -> {
                val degree = if (actualWeight.equals(0f)) {
                    1.0f
                } else {
                    (abs(actualWeight - targetWeight) / actualWeight).toFloat()
                }
                val rightScale = balancedScale - tilt * degree
                val leftScale = balancedScale + tilt * degree
                leftWing.scaleY = leftScale
                rightWing.scaleY = rightScale
                rightSpecial.scaleY = rightScale
            }
            else -> {
                val degree = if (targetWeight.equals(0f)) {
                    1.0f
                } else {
                    (abs(actualWeight - targetWeight) / targetWeight).toFloat()
                }
                val rightScale = balancedScale + tilt * degree
                val leftScale = balancedScale - tilt * degree
                leftWing.scaleY = leftScale
                rightWing.scaleY = rightScale
                rightSpecial.scaleY = rightScale
            }
        }
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