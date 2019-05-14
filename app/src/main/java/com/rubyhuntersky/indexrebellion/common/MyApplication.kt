package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.main.MainPortals
import com.rubyhuntersky.indexrebellion.interactions.main.MainStory
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.RefreshHoldingsStory
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsPortal
import com.rubyhuntersky.indexrebellion.presenters.main.MainActivity
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.SwitchWell
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.login.ROBINHOOD_LOGIN
import com.rubyhuntersky.robinhood.login.RobinhoodLoginDialogFragment
import com.rubyhuntersky.robinhood.login.RobinhoodLoginInteraction
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.storage.PreferencesBook
import com.rubyhuntersky.indexrebellion.interactions.main.Action as MainAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action as RefreshholdingsAction
import com.rubyhuntersky.robinhood.login.Action as RobinhoodLoginAction
import com.rubyhuntersky.robinhood.login.Services as RobinhoodLoginServices

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        StockMarket.network = SharedHttpNetwork

        accessBook = PreferencesBook(this, "AccessBook", Access.serializer()) {
            Access(BuildConfig.ROBINHOOD_USERNAME, BuildConfig.ROBINHOOD_TOKEN)
        }
        rebellionBook = SharedRebellionBook.also { it.open(this) }



        AndroidEdge += MainStory(mainWell).also {
            // TODO Replace portals with edge calls
            val portals = MainPortals(
                constituentSearchPortal = ConstituentSearchPortal { MainActivity.currentActivity()!! },
                cashEditingPortal = object : Portal<Unit> {
                    override fun jump(carry: Unit) {
                        SharedCashEditingInteraction.sendAction(Action.Load)
                        MainActivity.currentActivity()?.supportFragmentManager?.let {
                            CashEditingDialogFragment.newInstance().show(it, "cash_editing")
                        }
                    }
                },
                correctionDetailPortal = CorrectionDetailsPortal { MainActivity.currentActivity()!! }
            )
            it.sendAction(
                com.rubyhuntersky.indexrebellion.interactions.main.Action.Start(
                    rebellionBook, portals
                )
            )
        }

        AndroidEdge += object : ProjectionSource {
            override val group: String = ROBINHOOD_LOGIN

            override fun <V, A> startProjection(
                fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long
            ) {
                RobinhoodLoginDialogFragment.new(key)
                    .show(fragmentActivity.supportFragmentManager, "$ROBINHOOD_LOGIN/Projection")
            }
        }
    }

    companion object {

        val mainWell = SwitchWell()
        lateinit var accessBook: Book<Access>
        lateinit var rebellionBook: Book<Rebellion>
        private val rbhApi = RbhApi.SHARED

        fun refreshHoldingsInteraction() = RefreshHoldingsStory(mainWell).also {
            it.sendAction(
                RefreshholdingsAction.Start(
                    token = accessBook.value.token,
                    api = rbhApi,
                    book = rebellionBook
                )
            )
        }

        fun robinhoodLoginInteraction() = RobinhoodLoginInteraction(mainWell).also {
            val services = RobinhoodLoginServices(rbhApi, accessBook)
            val start = RobinhoodLoginAction.Start(services)
            it.sendAction(start)
        }
    }
}