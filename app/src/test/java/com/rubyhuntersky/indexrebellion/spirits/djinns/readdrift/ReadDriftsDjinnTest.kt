package com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.MAIN_ACCOUNT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.interaction.core.BehaviorBook
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ReadDriftsDjinnTest {

    private val now = Date()
    private val tsla = InstrumentId("TSLA", InstrumentType.StockExchange)
    private val drift0 = DEFAULT_DRIFT
    private val drift1 = drift0
        .replace(
            InstrumentSample(
                tsla, "Tesla, Inc.", CashAmount(420.0), CashAmount(50000000000), now
            )
        )
        .replace(
            SpecificHolding(tsla, Custodian.Wallet, MAIN_ACCOUNT, BigDecimal.valueOf(3), now)
        )
    private val drift2 = drift1
        .replace(
            SpecificHolding(tsla, Custodian.Robinhood, MAIN_ACCOUNT, BigDecimal.valueOf(5), now)
        )

    @Test
    fun main() {
        val book = BehaviorBook(drift0)
        val test = ReadDriftsDjinn(book).toObservable(ReadDrifts).test()
        book.write(drift1)
        book.write(drift2)
        test.assertValues(drift0, drift1, drift2)
    }
}