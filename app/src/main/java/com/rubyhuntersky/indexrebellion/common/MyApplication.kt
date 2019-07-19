package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.indexrebellion.interactions.correctiondetails.enableCorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.main.MainPortals
import com.rubyhuntersky.indexrebellion.interactions.main.MainStory
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access2
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.enableRefreshHoldings
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory
import com.rubyhuntersky.indexrebellion.presenters.ChooseHoldingTypeFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.main.MainActivity
import com.rubyhuntersky.indexrebellion.presenters.updateshares.UpdateSharesDialogFragment
import com.rubyhuntersky.indexrebellion.projections.ClassifyInstrumentActivity
import com.rubyhuntersky.indexrebellion.projections.EditHoldingActivity
import com.rubyhuntersky.indexrebellion.projections.ViewHoldingActivity
import com.rubyhuntersky.indexrebellion.projections.ViewPlanActivity
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.indexrebellion.spirits.genies.DeleteGeneralHolding
import com.rubyhuntersky.indexrebellion.spirits.genies.showtoast.ShowToast
import com.rubyhuntersky.indexrebellion.spirits.genies.writedrift.WriteDrift
import com.rubyhuntersky.indexrebellion.spirits.genies.writeinstrumentplate.WriteInstrumentPlatingGenie
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.robinhood.login.FetchRbhAccessTokenGenie
import com.rubyhuntersky.robinhood.login.ReadAccess
import com.rubyhuntersky.robinhood.login.RobinhoodLoginDialogFragment
import com.rubyhuntersky.robinhood.login.WriteAccess
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.storage.SharedPreferencesBook
import com.rubyhuntersky.vx.android.logChanges
import kotlinx.serialization.UnstableDefault
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action as CashEditingAction
import com.rubyhuntersky.indexrebellion.interactions.main.Action as MainAction

class MyApplication : Application() {

    @UnstableDefault
    override fun onCreate() {
        super.onCreate()
        StockMarket.network = SharedHttpNetwork

        // Books
        rebellionBook = SharedRebellionBook.also { it.open(this) }
        val driftBook = SharedPreferencesBook(this, "DriftBook", Drift.serializer(), true) { DEFAULT_DRIFT }
        val accessBook = SharedPreferencesBook(this, "Access2Book", Access2.serializer()) { Access2() }

        val edge = AndroidEdge
        with(edge.lamp) {
            add(ShowToast.GENIE(this@MyApplication))
            enableCorrectionDetails(this)
            enableRefreshHoldings(this)
            MainStory.addSpiritsToLamp(this)
            add(ReadDriftsDjinn(driftBook))
            add(WriteInstrumentPlatingGenie(driftBook))
            add(WriteDrift.GENIE(driftBook))
            add(ReadAccess.GENIE(accessBook))
            add(WriteAccess.GENIE(accessBook))
            add(FetchRbhAccessTokenGenie)
            add(DeleteGeneralHolding.GENIE(driftBook))
        }

        ViewDriftStory()
            .logChanges(ViewDriftStory.groupId)
            .also {
                edge.addInteraction(it)
                it.sendAction((ViewDriftStory.Action.Init))
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
            UpdateSharesDialogFragment,
            ClassifyInstrumentActivity,
            EditHoldingActivity,
            ViewPlanActivity,
            ChooseHoldingTypeFragment
        )
    }

    companion object {
        val rbhApi = RbhApi.SHARED
        lateinit var rebellionBook: RebellionBook
    }
}