package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.rubyhuntersky.data.report.Correction
import com.rubyhuntersky.data.report.RebellionReport
import java.util.*

class ConclusionViewHolder(private val conclusionView: RecyclerView) : RecyclerView.ViewHolder(conclusionView) {

    fun render(
        refreshDate: Date,
        conclusion: RebellionReport.Conclusion,
        onAddConstituentClick: () -> Unit,
        onCorrectionDetailsClick: (Correction) -> Unit
    ) {
        with(conclusionView) {
            if (layoutManager == null) {
                layoutManager = object : LinearLayoutManager(context) {}
            }

            val adapter: CorrectionsRecyclerViewAdapter = adapter as? CorrectionsRecyclerViewAdapter
                ?: CorrectionsRecyclerViewAdapter(onAddConstituentClick, onCorrectionDetailsClick)
                    .also {
                        adapter = it
                    }

            Log.d(this.javaClass.simpleName, "conclusion: $conclusion")
            when (conclusion) {
                is RebellionReport.Conclusion.AddConstituent -> adapter.bind(emptyList(), refreshDate)
                is RebellionReport.Conclusion.RefreshPrices -> Unit
                is RebellionReport.Conclusion.Divest -> adapter.bind(conclusion.corrections, refreshDate)
                is RebellionReport.Conclusion.Maintain -> adapter.bind(conclusion.corrections, refreshDate)
            }
        }
    }
}