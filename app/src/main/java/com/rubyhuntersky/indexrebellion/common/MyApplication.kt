package com.rubyhuntersky.indexrebellion.common

import android.app.Application
import android.util.Log
import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.data.common.DateSerializer
import com.rubyhuntersky.stockcatalog.StockMarket
import java.util.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("MyApplication", "Date serializer: ${DateSerializer.descriptor}")

        StockMarket.network = SharedHttpNetwork
        SharedRebellionBook.open(this)
    }

    companion object {
        val RANDOM = Random()
    }

}