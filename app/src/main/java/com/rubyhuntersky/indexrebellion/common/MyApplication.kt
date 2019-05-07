package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import com.rubyhuntersky.indexrebellion.BuildConfig
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.robinhood.api.RbhApi
import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
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
    }

    companion object {
        val RANDOM = Random()
        val rbhApi = RbhApi.SHARED
        lateinit var accessBook: Book<Access>
    }
}