package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.main.MainStory
import com.rubyhuntersky.indexrebellion.interactions.main.MainPortals
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.RefreshHoldingsStory
import com.rubyhuntersky.indexrebellion.presenters.cashediting.CashEditingDialogFragment
import com.rubyhuntersky.indexrebellion.presenters.cashediting.SharedCashEditingInteraction
import com.rubyhuntersky.indexrebellion.presenters.constituentsearch.ConstituentSearchPortal
import com.rubyhuntersky.indexrebellion.presenters.correctiondetails.CorrectionDetailsPortal
import com.rubyhuntersky.indexrebellion.presenters.main.MainActivity
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.interaction.core.SwitchWell
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.storage.PreferencesBook
import java.util.*
import com.rubyhuntersky.indexrebellion.interactions.main.Action as MainAction
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action as RefreshholdingsAction

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
    }

    companion object {
        val RANDOM = Random()
        private val rbhApi = RbhApi.SHARED
        val mainWell = SwitchWell()
        lateinit var accessBook: Book<Access>
        lateinit var rebellionBook: Book<Rebellion>

        fun refreshHoldingsInteraction() = RefreshHoldingsStory(mainWell).also {
            it.sendAction(
                RefreshholdingsAction.Start(
                    token = accessBook.value.token,
                    api = rbhApi,
                    book = rebellionBook,
                    id = RANDOM.nextLong()
                )
            )
        }
    }
}