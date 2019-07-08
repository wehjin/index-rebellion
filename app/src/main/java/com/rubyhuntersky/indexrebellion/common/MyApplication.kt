package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.enableCorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.main.MainPortals
import com.rubyhuntersky.indexrebellion.interactions.main.MainStory
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.enableRefreshHoldings
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.main.MainActivity
import com.rubyhuntersky.indexrebellion.presenters.updateshares.UpdateSharesDialogFragment
import com.rubyhuntersky.indexrebellion.projections.holdings.ViewHoldingActivity
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.indexrebellion.spirits.showtoast.ShowToastGenie
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.login.RobinhoodLoginDialogFragment
import com.rubyhuntersky.robinhood.login.enableRobinhoodLogin
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.storage.PreferencesBook
import com.rubyhuntersky.vx.android.logChanges
import kotlinx.serialization.UnstableDefault
import java.math.BigDecimal
import java.util.*
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action as CashEditingAction
import com.rubyhuntersky.indexrebellion.interactions.main.Action as MainAction
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.Action as ViewdriftAction
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Action as ViewholdingAction

class MyApplication : Application() {

    @UnstableDefault
    override fun onCreate() {
        super.onCreate()
        StockMarket.network = SharedHttpNetwork

        // Books
        accessBook = PreferencesBook(this, "AccessBook", Access.serializer()) {
            Access(BuildConfig.ROBINHOOD_USERNAME, BuildConfig.ROBINHOOD_TOKEN)
        }
        rebellionBook = SharedRebellionBook.also { it.open(this) }
        val driftBook = BehaviorBook(DRIFT)

        val edge = AndroidEdge
        with(edge.lamp) {
            add(ShowToastGenie(this@MyApplication))
            enableCorrectionDetails(this)
            enableRefreshHoldings(this)
            enableRobinhoodLogin(this)
            MainStory.addSpiritsToLamp(this)
            add(ReadDriftsDjinn(driftBook))
        }

        ViewDriftStory()
            .logChanges(ViewDriftStory.groupId)
            .also {
                edge.addInteraction(it)
                it.sendAction((ViewdriftAction.Init))
            }

        ViewHoldingStory()
            .logChanges(ViewHoldingStory.groupId)
            .also {
                edge.addInteraction(it)
                it.sendAction(ViewholdingAction.Init(InstrumentId("TSLA", InstrumentType.StockExchange)))
            }

        MainStory().also { story ->
            edge.addInteraction(story)
            // TODO Replace portals with edge calls
            story.sendAction(MainAction.Start(
                rebellionBook = rebellionBook,
                portals = MainPortals(
                    constituentSearchPortal = ConstituentSearchPortal { MainActivity.currentActivity()!! },
                    cashEditingPortal = object : Portal<Unit> {
                        override fun jump(carry: Unit) {
                            SharedCashEditingInteraction.sendAction(CashEditingAction.Load)
                            MainActivity.currentActivity()?.supportFragmentManager?.let {
                                CashEditingDialogFragment.newInstance().show(it, "cash_editing")
                            }
                        }
                    }
                )
            ))
        }

        edge.addProjectionBuilder(
            ViewHoldingActivity,
            RobinhoodLoginDialogFragment,
            CorrectionDetailsDialogFragment,
            UpdateSharesDialogFragment
        )
    }

    companion object {
        val rbhApi = RbhApi.SHARED

        lateinit var accessBook: Book<Access>
        lateinit var rebellionBook: RebellionBook

        private val TSLA_ID = InstrumentId("TSLA", InstrumentType.StockExchange)
        private val SQ_ID = InstrumentId("SQ", InstrumentType.StockExchange)
        private val FAR_PAST = Date(0)
        private val DRIFT = DEFAULT_DRIFT
            .replaceSample(
                InstrumentSample(
                    TSLA_ID, "Tesla, Inc.", CashAmount(42), CashAmount(420000000),
                    FAR_PAST
                )
            )
            .replaceSample(
                InstrumentSample(
                    SQ_ID, "Square, Inc.", CashAmount(64), CashAmount(64000000),
                    FAR_PAST
                )
            )
            .replaceHolding(
                SpecificHolding(
                    TSLA_ID, Custodian.Robinhood, BigDecimal.valueOf(10),
                    FAR_PAST
                )
            )
            .replaceHolding(
                SpecificHolding(
                    SQ_ID, Custodian.Etrade, BigDecimal.valueOf(100),
                    FAR_PAST
                )
            )
    }
}