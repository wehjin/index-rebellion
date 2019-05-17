package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.CORRECTION_DETAILS
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.CorrectionDetailsStory
import com.rubyhuntersky.indexrebellion.interactions.main.MainPortals
import com.rubyhuntersky.indexrebellion.interactions.main.MainStory
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.RefreshHoldingsStory
import com.rubyhuntersky.indexrebellion.interactions.updateshares.UPDATE_SHARES
import com.rubyhuntersky.indexrebellion.interactions.updateshares.UpdateSharesStory
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.main.MainActivity
import com.rubyhuntersky.indexrebellion.presenters.updateshares.UpdateSharesDialogFragment
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
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Action as CorrectionDetailsAction
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Culture as CorrectionDetailsCulture
import com.rubyhuntersky.indexrebellion.interactions.main.Action as MainAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action as RefreshHoldingsAction
import com.rubyhuntersky.indexrebellion.interactions.updateshares.Action as UpdateSharesAction
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



        AndroidEdge += MainStory(mainWell).also { story ->
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
                }
            )
            val start = MainAction.Start(rebellionBook, portals)
            story.sendAction(start)
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
        AndroidEdge += object : ProjectionSource {
            override val group: String = CORRECTION_DETAILS

            override fun <V, A> startProjection(
                fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long
            ) {
                val dialogFragment = CorrectionDetailsDialogFragment.new(key)
                dialogFragment.show(fragmentActivity.supportFragmentManager, "$CORRECTION_DETAILS/Projection")
            }
        }
        AndroidEdge += object : ProjectionSource {
            override val group: String = UPDATE_SHARES

            override fun <V, A> startProjection(
                fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long
            ) {
                val dialogFragment = UpdateSharesDialogFragment.new(key)
                dialogFragment.show(fragmentActivity.supportFragmentManager, "$UPDATE_SHARES/Projection")
            }
        }

    }

    companion object {

        val mainWell = SwitchWell()
        lateinit var accessBook: Book<Access>
        lateinit var rebellionBook: Book<Rebellion>
        private val rbhApi = RbhApi.SHARED

        fun refreshHoldingsInteraction() =
            RefreshHoldingsStory(mainWell)
                .also {
                    it.sendAction(
                        RefreshHoldingsAction.Start(
                            token = accessBook.value.token,
                            api = rbhApi,
                            book = rebellionBook
                        )
                    )
                }

        fun robinhoodLoginInteraction() =
            RobinhoodLoginInteraction(mainWell)
                .also {
                    val services = RobinhoodLoginServices(rbhApi, accessBook)
                    val start = RobinhoodLoginAction.Start(services)
                    it.sendAction(start)
                }

        fun correctionDetailsStory(details: CorrectionDetails): CorrectionDetailsStory =
            CorrectionDetailsStory(mainWell)
                .also {
                    val correctionDetailsBook = CorrectionDetailsBook(details, SharedRebellionBook)
                    val start = CorrectionDetailsAction.Start(CorrectionDetailsCulture(correctionDetailsBook))
                    it.sendAction(start)
                }

        fun updateSharesStory(assetSymbol: AssetSymbol) =
            UpdateSharesStory(mainWell)
                .also {
                    val start = UpdateSharesAction.Start(rebellionBook, assetSymbol)
                    it.sendAction(start)
                }
    }
}