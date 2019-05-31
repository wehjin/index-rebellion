package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.report.Correction
import kotlinx.android.synthetic.main.view_corrections_footer.view.*
import java.util.*
import kotlin.math.max

class CorrectionsRecyclerViewAdapter(
    private val onAddConstituentClick: () -> Unit,
    private val onCorrectionDetailsClick: (Correction) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class CorrectionsViewType { HEADER, FOOTER, BODY }

    private var corrections: List<Correction> = emptyList()
    private var correctionsHighWeight: Double = corrections.highWeight
    private var refreshDate: Date = Date(0)

    override fun getItemCount(): Int = 2 + corrections.size

    override fun getItemViewType(position: Int): Int = getViewType(position).ordinal

    private fun getViewType(position: Int): CorrectionsViewType {
        return when {
            position == 0 -> CorrectionsViewType.HEADER
            position <= corrections.size -> CorrectionsViewType.BODY
            else -> CorrectionsViewType.FOOTER
        }
    }

    fun bind(corrections: List<Correction>, refreshDate: Date) {
        this.corrections = corrections
        this.refreshDate = refreshDate
        correctionsHighWeight = corrections.highWeight
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val correctionsViewType = CorrectionsViewType.values()[viewType]
        val layoutRes = when (correctionsViewType) {
            CorrectionsViewType.HEADER -> R.layout.view_corrections_header
            CorrectionsViewType.BODY -> R.layout.view_corrections_body
            CorrectionsViewType.FOOTER -> R.layout.view_corrections_footer
        }
        val itemView = layoutInflater.inflate(layoutRes, parent, false)
        return when (correctionsViewType) {
            CorrectionsViewType.BODY -> CorrectionBodyViewHolder(itemView)
            else -> object : RecyclerView.ViewHolder(itemView) {}
        }
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getViewType(position)) {
            CorrectionsViewType.FOOTER -> viewHolder.itemView.plusConstituentButton.setOnClickListener { onAddConstituentClick() }
            CorrectionsViewType.BODY -> {
                val correction = corrections[position - 1]
                (viewHolder as CorrectionBodyViewHolder).bindCorrection(
                    correction,
                    correctionsHighWeight,
                    onCorrectionDetailsClick
                )
            }
            CorrectionsViewType.HEADER -> Unit
        }
    }

    private val List<Correction>.highWeight: Double
        get() = if (this.isEmpty()) {
            0.0
        } else {
            this.map { it.highWeight }.fold(0.0, ::max)
        }
}