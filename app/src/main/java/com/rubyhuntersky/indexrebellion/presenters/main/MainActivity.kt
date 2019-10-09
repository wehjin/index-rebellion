package com.rubyhuntersky.indexrebellion.presenters.main

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.rbhApi
import com.rubyhuntersky.indexrebellion.common.MyApplication.Companion.rebellionBook
import com.rubyhuntersky.indexrebellion.interactions.main.Action
import com.rubyhuntersky.indexrebellion.interactions.main.MainStory.Companion.groupId
import com.rubyhuntersky.indexrebellion.interactions.main.Vision
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.RefreshHoldingsStory
import com.rubyhuntersky.indexrebellion.projections.drift.DriftActivity
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.PendingInteractions
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*
import java.text.SimpleDateFormat
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action as RefreshHoldingsAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Vision as RefreshHoldingsVision

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ActivityInteraction(groupId, this, this::renderVision))
    }

    private fun beta() {
        val intent = Intent(this, DriftActivity::class.java)
        startActivity(intent)
    }

    private fun refreshHoldings() {
        val interaction = RefreshHoldingsStory()
            .also {
                AndroidEdge.addInteraction(it)
                it.sendAction(RefreshHoldingsAction.Start(rbhApi, rebellionBook))
            }
        pendingInteractions.follow(interaction) { vision ->
            runOnUiThread {
                when (vision) {
                    is RefreshHoldingsVision.NewHoldings -> {
                        Log.d(groupId, "New holdings: ${vision.newHoldings}")
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
        Log.e(groupId, error.localizedMessage, error)
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        Log.d(groupId, "VISION: $vision")
        when (vision) {
            is Vision.Loading -> setContentView(R.id.mainLoading, R.layout.activity_main_loading)
            is Vision.Viewing -> {
                setContentView(R.id.mainViewing, R.layout.activity_main_viewing)
                setSupportActionBar(toolbar)
                val report = vision.rebellionReport
                supportActionBar!!.title = getString(R.string.funding)
                FundingViewHolder(fundingView)
                    .render(
                        report.funding,
                        onNewInvestmentClick = { sendAction(Action.OpenCashEditor) }
                    )
                timestampTextView.text = SimpleDateFormat.getDateTimeInstance().format(report.refreshDate)
                correctionsAddButton.setOnClickListener { sendAction(Action.FindConstituent) }
                ConclusionViewHolder(correctionsRecyclerView).render(
                    refreshDate = report.refreshDate,
                    conclusion = report.conclusion,
                    onCorrectionDetailsClick = { sendAction(Action.OpenCorrectionDetails(it)) }
                )
                correctionsSwipeToRefresh.setOnRefreshListener { sendAction(Action.Refresh) }
                if (!vision.isRefreshing) {
                    correctionsSwipeToRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setContentView(@IdRes viewId: Int, @LayoutRes layoutId: Int) {
        findViewById<View>(viewId)?.let {
            // Content view already exists
        } ?: setContentView(layoutId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.refreshHoldings -> true.also { refreshHoldings() }
            R.id.beta -> true.also { beta() }
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
