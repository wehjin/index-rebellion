package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.MyApplication
import com.rubyhuntersky.indexrebellion.interactions.main.Action
import com.rubyhuntersky.indexrebellion.interactions.main.MAIN_INTERACTION_TAG
import com.rubyhuntersky.indexrebellion.interactions.main.Vision
import com.rubyhuntersky.interaction.android.NamedInteractionActivity
import com.rubyhuntersky.interaction.core.PendingInteractions
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*
import java.text.SimpleDateFormat
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action as CashEditingAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action as RefreshHoldingsAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Vision as RefreshHoldingsVision

class MainActivity : NamedInteractionActivity<Vision, Action>() {

    override val name: String = MAIN_INTERACTION_TAG

    private fun refreshHoldings() {
        val interaction = MyApplication.refreshHoldingsInteraction()
        pendingInteractions.follow(interaction) { vision ->
            runOnUiThread {
                when (vision) {
                    is RefreshHoldingsVision.NewHoldings -> {
                        Log.d(name, "New holdings: ${vision.newHoldings}")
                        Toast.makeText(this, "Updated holdings", Toast.LENGTH_SHORT).show()
                    }
                    is RefreshHoldingsVision.Error -> presentError(vision.error)
                    else -> throw NotImplementedError()
                }
            }
        }
    }

    private val pendingInteractions = PendingInteractions()

    private fun presentError(error: Throwable) {
        Log.e(name, error.localizedMessage, error)
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    override fun renderVision(vision: Vision) {
        Log.d(name, "VISION: $vision")
        when (vision) {
            is Vision.Loading -> setContentView(R.id.mainLoading, R.layout.activity_main_loading)
            is Vision.Viewing -> {
                setContentView(R.id.mainViewing, R.layout.activity_main_viewing)
                setSupportActionBar(toolbar)
                val report = vision.rebellionReport
                supportActionBar!!.title = getString(R.string.funding)
                FundingViewHolder(fundingView)
                    .render(report.funding, onNewInvestmentClick = {
                        interaction.sendAction(Action.OpenCashEditor)
                    })
                timestampTextView.text = SimpleDateFormat.getDateTimeInstance().format(report.refreshDate)
                correctionsAddButton.setOnClickListener { interaction.sendAction(Action.FindConstituent) }
                ConclusionViewHolder(correctionsRecyclerView).render(
                    refreshDate = report.refreshDate,
                    conclusion = report.conclusion,
                    onCorrectionDetailsClick = { interaction.sendAction(Action.OpenCorrectionDetails(it)) }
                )
                correctionsSwipeToRefresh.setOnRefreshListener {
                    interaction.sendAction(Action.Refresh)
                }
                if (!vision.isRefreshing) {
                    correctionsSwipeToRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setContentView(@IdRes viewId: Int, @LayoutRes layoutId: Int) {
        if (findViewById<View>(viewId) == null) {
            setContentView(layoutId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.refreshHoldings -> true.also { refreshHoldings() }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onStart() {
        super.onStart()
        mainActivity = this
    }

    override fun onStop() {
        pendingInteractions.dispose()
        if (mainActivity == this) {
            mainActivity = null
        }
        super.onStop()
    }

    companion object {
        private var mainActivity: MainActivity? = null

        // TODO Remove when MainPortals is gone
        fun currentActivity(): MainActivity? {
            return mainActivity
        }
    }
}
