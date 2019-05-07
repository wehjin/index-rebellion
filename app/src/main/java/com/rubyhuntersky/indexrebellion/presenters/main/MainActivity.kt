package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.common.MyApplication
import com.rubyhuntersky.indexrebellion.interactions.main.Action
import com.rubyhuntersky.indexrebellion.interactions.main.MainInteraction
import com.rubyhuntersky.indexrebellion.interactions.main.Vision
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsPortal
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.Projector
import com.rubyhuntersky.robinhood.login.RobinhoodLoginPortal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action as CashEditingAction

class MainActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private fun refreshHoldings() {
        val token = MyApplication.accessBook.value.token
        holdingsFetch = MyApplication.rbhApi.holdings(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(tag, "RbhHoldingsResult: $result")
                val text = result.positions
                    .map { position ->
                        val quote = result.quotesByInstrumentLocation[position.instrumentLocation]
                        "${position.quantity} shares of ${quote?.symbol ?: "Unknown"}"
                    }
                    .joinToString(", ")
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            }, {
                Log.e(tag, it.localizedMessage, it)
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            })
    }

    private var holdingsFetch: Disposable? = null

    private val projector = Projector(
        mainInteraction,
        observeOn = AndroidSchedulers.mainThread(),
        log = { Log.d(this.javaClass.simpleName, "VISION: $it") }
    ).addComponent(object : Projector.Component<Vision, Vision.Loading, Action> {
        override fun convert(vision: Vision): Vision.Loading? = vision as? Vision.Loading
        override fun render(vision: Vision.Loading, sendAction: (Action) -> Unit) {
            setContentView(R.id.mainLoading, R.layout.activity_main_loading)
        }
    }).addComponent(object : Projector.Component<Vision, Vision.Viewing, Action> {
        override fun convert(vision: Vision) = vision as? Vision.Viewing
        override fun render(vision: Vision.Viewing, sendAction: (Action) -> Unit) {
            setContentView(R.id.mainViewing, R.layout.activity_main_viewing)
            setSupportActionBar(toolbar)
            val report = vision.rebellionReport
            supportActionBar!!.title = getString(R.string.funding)
            FundingViewHolder(fundingView)
                .render(report.funding, onNewInvestmentClick = {
                    sendAction(Action.OpenCashEditor)
                })
            ConclusionViewHolder(correctionsRecyclerView).render(
                refreshDate = report.refreshDate,
                conclusion = report.conclusion,
                onAddConstituentClick = { sendAction(Action.FindConstituent) },
                onCorrectionDetailsClick = { sendAction(Action.OpenCorrectionDetails(it)) }
            )
            correctionsSwipeToRefresh.setOnRefreshListener {
                sendAction(Action.Refresh)
            }
            if (!vision.isRefreshing) {
                correctionsSwipeToRefresh.isRefreshing = false
            }
        }
    })

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.robinhood -> true.also { RobinhoodLoginPortal(this).open() }
            R.id.refreshHoldings -> true.also { refreshHoldings() }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        mainActivity = this
        projector.start()
    }

    override fun onStop() {
        holdingsFetch?.dispose()
        projector.stop()
        if (mainActivity == this) {
            mainActivity = null
        }
        super.onStop()
    }

    private fun setContentView(@IdRes viewId: Int, @LayoutRes layoutId: Int) {
        if (findViewById<View>(viewId) == null) {
            setContentView(layoutId)
        }
    }

    companion object {
        private var mainActivity: MainActivity? = null

        private val mainInteraction = MainInteraction(
            rebellionBook = SharedRebellionBook,
            constituentSearchPortal = ConstituentSearchPortal { mainActivity!! },
            cashEditingPortal = object : Portal<Unit> {
                override fun jump(carry: Unit) {
                    SharedCashEditingInteraction.sendAction(CashEditingAction.Load)
                    mainActivity?.supportFragmentManager?.let {
                        CashEditingDialogFragment.newInstance().show(it, "cash_editing")
                    }
                }
            },
            correctionDetailPortal = CorrectionDetailsPortal { mainActivity!! }
        )
    }
}
