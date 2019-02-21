package com.rubyhuntersky.indexrebellion.presenters.main

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsPortal
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.main.Action
import com.rubyhuntersky.interaction.main.MainInteraction
import com.rubyhuntersky.interaction.main.Vision
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*

class MainActivity : AppCompatActivity() {

    private val composite = CompositeDisposable()

    override fun onStart() {
        mainActivity = this
        mainInteraction.visionStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(this.javaClass.simpleName, "VISION: $it")
                when (it) {
                    is Vision.Loading -> {
                        setContentView(R.id.mainLoading, R.layout.activity_main_loading)
                    }
                    is Vision.Viewing -> {
                        setContentView(R.id.mainViewing, R.layout.activity_main_viewing)
                        setSupportActionBar(toolbar)
                        renderViewing(it)
                    }
                }
            }
            .addTo(composite)
        super.onStart()
    }

    private fun renderViewing(viewing: Vision.Viewing) {
        val report = viewing.rebellionReport
        supportActionBar!!.title = getString(R.string.funding)
        FundingViewHolder(fundingView)
            .render(report.funding, onNewInvestmentClick = {
                mainInteraction.sendAction(Action.OpenCashEditor)
            })

        ConclusionViewHolder(correctionsRecyclerView).render(
            refreshDate = report.refreshDate,
            conclusion = report.conclusion,
            onAddConstituentClick = { mainInteraction.sendAction(Action.FindConstituent) },
            onCorrectionDetailsClick = { mainInteraction.sendAction(Action.OpenCorrectionDetails(it)) }
        )

        correctionsSwipeToRefresh.setOnRefreshListener {
            mainInteraction.sendAction(Action.Refresh)
        }
        if (!viewing.isRefreshing) {
            correctionsSwipeToRefresh.isRefreshing = false
        }
    }

    override fun onStop() {
        composite.clear()
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
                    SharedCashEditingInteraction.reset()
                    mainActivity?.supportFragmentManager?.let {
                        CashEditingDialogFragment.newInstance().show(it, "cash_editing")
                    }
                }
            },
            correctionDetailPortal = CorrectionDetailsPortal { mainActivity!! }
        )
    }
}
