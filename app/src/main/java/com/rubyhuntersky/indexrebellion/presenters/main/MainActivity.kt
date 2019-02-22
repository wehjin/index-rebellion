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
import com.rubyhuntersky.interaction.core.Projector
import com.rubyhuntersky.interaction.main.Action
import com.rubyhuntersky.interaction.main.MainInteraction
import com.rubyhuntersky.interaction.main.Vision
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main_viewing.*
import kotlinx.android.synthetic.main.view_funding.*

class MainActivity : AppCompatActivity() {

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

    override fun onStart() {
        super.onStart()
        mainActivity = this
        projector.start()
    }

    override fun onStop() {
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
