package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Action
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.RefreshHoldingsInteraction
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.SwitchWell
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.stockcatalog.StockMarket
import com.rubyhuntersky.storage.PreferencesBook
import java.util.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        accessBook = PreferencesBook(this, "AccessBook", Access.serializer()) {
            Access(BuildConfig.ROBINHOOD_USERNAME, BuildConfig.ROBINHOOD_TOKEN)
        }
        StockMarket.network = SharedHttpNetwork
        SharedRebellionBook.open(this)
        rebellionBook = SharedRebellionBook
    }

    companion object {
        val RANDOM = Random()
        private val rbhApi = RbhApi.SHARED
        private val mainWell = SwitchWell()
        lateinit var accessBook: Book<Access>
        lateinit var rebellionBook: Book<Rebellion>

        fun refreshHoldingsStory() = RefreshHoldingsInteraction(mainWell)
            .also {
                val start = Action.Start(
                    token = accessBook.value.token,
                    api = rbhApi,
                    book = rebellionBook,
                    id = RANDOM.nextLong()
                )
                it.sendAction(start)
            }
    }
}