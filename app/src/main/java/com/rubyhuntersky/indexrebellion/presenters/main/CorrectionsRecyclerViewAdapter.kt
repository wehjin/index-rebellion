package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.report.Correction
import java.util.*
import kotlin.math.max

class CorrectionsRecyclerViewAdapter(
    private val onCorrectionDetailsClick: (Correction) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class CorrectionsViewType { BODY }

    private var corrections: List<Correction> = emptyList()
    private var correctionsHighWeight: Double = corrections.highWeight
    private var refreshDate: Date = Date(0)

    override fun getItemCount(): Int = corrections.size

    override fun getItemViewType(position: Int): Int = getViewType(position).ordinal

    private fun getViewType(position: Int): CorrectionsViewType {
        return when {
            position >= 0 && position < corrections.size -> CorrectionsViewType.BODY
            else -> throw IllegalStateException("Invalid position $position")
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
            CorrectionsViewType.BODY -> R.layout.view_corrections_body
        }
        val itemView = layoutInflater.inflate(layoutRes, parent, false)
        return when (correctionsViewType) {
            CorrectionsViewType.BODY -> CorrectionBodyViewHolder(itemView)
        }
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getViewType(position)) {
            CorrectionsViewType.BODY -> {
                val correction = corrections[position]
                (viewHolder as CorrectionBodyViewHolder).bindCorrection(
                    correction,
                    correctionsHighWeight,
                    onCorrectionDetailsClick
                )
            }
        }
    }

    private val List<Correction>.highWeight: Double
        get() = if (this.isEmpty()) {
            0.0
        } else {
            this.map { it.highWeight }.fold(0.0, ::max)
        }
}