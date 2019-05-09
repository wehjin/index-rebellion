package com.rubyhuntersky.indexrebellion.data.assets

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.common.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PriceSample(
    val cashAmount: CashAmount,
    @Serializable(with = DateSerializer::class) val date: Date
)
