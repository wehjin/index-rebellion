package com.rubyhuntersky.indexrebellion.data.techtonic.vault

import com.rubyhuntersky.indexrebellion.data.common.BigDecimalSerializer
import com.rubyhuntersky.indexrebellion.data.common.DateSerializer
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.*

@Serializable
data class SpecificHolding(
    val instrumentId: InstrumentId,
    val custodian: Custodian,
    val custodianAccount: CustodianAccount,
    @Serializable(with = BigDecimalSerializer::class)
    val size: BigDecimal,
    @Serializable(with = DateSerializer::class)
    val lastModified: Date
) {
    fun isRival(other: Any?): Boolean = when (other) {
        is SpecificHolding -> {
            instrumentId == other.instrumentId &&
                    custodian == other.custodian &&
                    custodianAccount == other.custodianAccount
        }
        else -> false
    }

    fun isAlly(other: Any?) = !isRival(other)

    fun equalsIgnoringModifyDate(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpecificHolding) return false
        if (instrumentId != other.instrumentId) return false
        if (custodian != other.custodian) return false
        if (custodianAccount != other.custodianAccount) return false
        if (size != other.size) return false
        return true
    }
}

