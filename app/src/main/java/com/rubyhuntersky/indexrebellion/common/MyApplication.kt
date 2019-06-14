package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.CORRECTION_DETAILS
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.enableCorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.main.*
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.enableRefreshHoldings
import com.rubyhuntersky.indexrebellion.interactions.updateshares.UPDATE_SHARES
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
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.login.ROBINHOOD_LOGIN
import com.rubyhuntersky.robinhood.login.RobinhoodLoginDialogFragment
import com.rubyhuntersky.robinhood.login.enableRobinhoodLogin
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.storage.PreferencesBook
import com.rubyhuntersky.vx.coop.additions.Span
import kotlinx.serialization.UnstableDefault
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Action as CorrectionDetailsAction
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.Culture as CorrectionDetailsCulture
import com.rubyhuntersky.indexrebellion.interactions.main.Action as MainAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action as RefreshHoldingsAction
import com.rubyhuntersky.indexrebellion.interactions.updateshares.Action as UpdateSharesAction
import com.rubyhuntersky.robinhood.login.Action as RobinhoodLoginAction
import com.rubyhuntersky.robinhood.login.Services as RobinhoodLoginServices

class MyApplication : Application() {

    @UnstableDefault
    override fun onCreate() {
        super.onCreate()
        standardMarginSpan = Span.Absolute(resources.getDimensionPixelSize(R.dimen.standard_margin_size))

        StockMarket.network = SharedHttpNetwork

        accessBook = PreferencesBook(this, "AccessBook", Access.serializer()) {
            Access(BuildConfig.ROBINHOOD_USERNAME, BuildConfig.ROBINHOOD_TOKEN)
        }
        rebellionBook = SharedRebellionBook.also { it.open(this) }

        val edge = AndroidEdge
        with(edge.lamp) {
            enableCorrectionDetails(this)
            enableRefreshHoldings(this)
            enableMainStory(this)
            enableRobinhoodLogin(this)
        }

        AndroidEdge += MainStory().also { story ->
            edge.addInteraction(story)

            // TODO Replace portals with edge calls
            story.sendAction(MainAction.Start(
                rebellionBook = rebellionBook,
                portals = MainPortals(
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
            ))
        }

        edge.addProjectionBuilder(
            object : ProjectionSource {
                override val group: String = ROBINHOOD_LOGIN

                override fun <V, A> startProjection(
                    fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long
                ) {
                    RobinhoodLoginDialogFragment.new(key)
                        .show(fragmentActivity.supportFragmentManager, "$ROBINHOOD_LOGIN/Projection")
                }
            },
            object : ProjectionSource {
                override val group: String = CORRECTION_DETAILS

                override fun <V, A> startProjection(
                    fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long
                ) {
                    val dialogFragment = CorrectionDetailsDialogFragment.new(key)
                    dialogFragment.show(fragmentActivity.supportFragmentManager, "$CORRECTION_DETAILS/Projection")
                }
            },
            object : ProjectionSource {
                override val group: String = UPDATE_SHARES

                override fun <V, A> startProjection(
                    fragmentActivity: FragmentActivity, interaction: Interaction<V, A>, key: Long
                ) {
                    val dialogFragment = UpdateSharesDialogFragment.new(key)
                    dialogFragment.show(fragmentActivity.supportFragmentManager, "$UPDATE_SHARES/Projection")
                }
            }
        )
    }

    companion object {
        lateinit var accessBook: Book<Access>
        lateinit var rebellionBook: RebellionBook
        lateinit var standardMarginSpan: Span
        val rbhApi = RbhApi.SHARED
    }
}