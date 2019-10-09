package com.rubyhuntersky.indexrebellion.presenters.main

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.cash.CashEquivalent
import com.rubyhuntersky.indexrebellion.data.report.RebellionReport
import com.rubyhuntersky.indexrebellion.data.toStatString
import kotlinx.android.synthetic.main.view_funding.view.*


class FundingViewHolder(fundingView: View) : RecyclerView.ViewHolder(fundingView) {

    fun render(funding: RebellionReport.Funding, onNewInvestmentClick: () -> Unit) {
        Log.d(this.javaClass.simpleName, "FUNDING: $funding")
        renderNewInvestment(funding.newInvestment, onNewInvestmentClick)
        itemView.currentInvestmentStatisticView.text = funding.currentInvestment.toDollarString()
        itemView.goalInvestmentStatisticView.text = funding.fullInvestment.toDollarString()
    }

    private fun renderNewInvestment(newInvestment: CashAmount, onNewInvestmentClick: () -> Unit) {
        with(itemView.newInvestmentStatisticView) {
            labelText = newInvestment.toCashInOrOut()
            text = newInvestment.toDollarString()
        }
        itemView.newInvestmentOperatorTextView.text = newInvestment.toPlusOrMinus()
        itemView.newInvestmentButton.setOnClickListener { onNewInvestmentClick() }
    }

    private fun CashAmount.toPlusOrMinus() = getString(if (toDouble() >= 0) R.string.plus else R.string.minus)
    private fun CashAmount.toCashInOrOut() = getString(if (toDouble() >= 0) R.string.deposit else R.string.withdrawal)
    private fun CashAmount.toDollarString() = toDouble().toStatString().toDollarString()

    private fun CashEquivalent.toDollarString(): CharSequence =
        toDouble()?.toStatString()?.toDollarString() ?: getString(R.string.unknown_quantity)

    private fun String.toDollarString(): String =
        context.getString(if (length == 1) R.string.dollar_space_format else R.string.dollar_format, this)

    private val context = itemView.context

    private fun getString(stringId: Int): String = context.getString(stringId)
}
