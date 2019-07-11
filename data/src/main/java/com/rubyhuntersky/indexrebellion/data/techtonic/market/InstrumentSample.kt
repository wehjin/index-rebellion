package com.rubyhuntersky.indexrebellion.data.techtonic.market

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.common.DateSerializer
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class InstrumentSample(
    val instrumentId: InstrumentId,
    val instrumentName: String,
    val sharePrice: CashAmount,
    val macroPrice: CashAmount,
    @Serializable(with = DateSerializer::class)
    val sampleDate: Date
) {
    fun setPrice(price: CashAmount, date: Date?) =
        copy(sharePrice = price, sampleDate = date ?: sampleDate)
}