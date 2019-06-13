package com.rubyhuntersky.indexrebellion.presenters.constituentsearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.android.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.indexrebellion.interactions.constituentsearch.Action
import com.rubyhuntersky.indexrebellion.interactions.constituentsearch.Vision
import kotlinx.android.synthetic.main.fragment_constituent_search.*


class ConstituentSearchDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
    layoutRes = R.layout.fragment_constituent_search,
    directInteraction = SharedConstituentSearch
) {

    override fun render(vision: Vision) {
        when (vision) {
            is Vision.Idle -> renderIdle()
            is Vision.Search -> renderSearch(vision)
            is Vision.Finished -> dismiss()
        }
    }

    private fun renderIdle() {
        symbolTextView.text = getString(R.string.choose_a_stock)
        marketCapTextView.text = null
        saveButton.visibility = View.INVISIBLE
        searchEditText.text = null
        if (renderedVision == null) {
            searchEditText.requestFocus()
        }
    }

    private fun renderSearch(search: Vision.Search) {
        val candidates = search.candidates
        if (candidates == null) {
            symbolTextView.text = getString(R.string.searching)
            marketCapTextView.text = null
            saveButton.visibility = View.INVISIBLE
        } else {
            val candidate = candidates.firstOrNull()
            if (candidate == null) {
                symbolTextView.text = getString(R.string.no_results_found)
                marketCapTextView.text = null
                saveButton.visibility = View.INVISIBLE
            } else {
                symbolTextView.text = candidate.description
                marketCapTextView.text = "$${candidate.marketWeight.toStatString()}"
                saveButton.visibility = View.VISIBLE
            }
        }
        with(searchEditText) {
            val activeSearchTerm = search.searchTerm.toLowerCase()
            if (text.toString().toLowerCase() != activeSearchTerm) {
                setText(activeSearchTerm)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                sendAction(Action.BeginSearch(s?.toString() ?: ""))
            }
        })
        saveButton.setOnClickListener {
            sendAction(Action.AddConstituent)
        }
    }

    companion object {
        fun newInstance(): ConstituentSearchDialogFragment = ConstituentSearchDialogFragment()
    }
}