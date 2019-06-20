package com.rubyhuntersky.indexrebellion.spirits.readdrift

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.interaction.core.BehaviorBook
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class ReadDriftsDjinnTest {

    private val now = Date()
    private val tsla = InstrumentId("TSLA", InstrumentType.StockExchange)
    private val drift0 = DEFAULT_DRIFT
    private val drift1 = drift0
        .replaceSample(
            InstrumentSample(
                tsla, "Tesla, Inc.", CashAmount(420.0), CashAmount(50000000000), now
            )
        )
        .replaceHolding(
            SpecificHolding(tsla, Custodian.None, BigDecimal.valueOf(3), now)
        )
    private val drift2 = drift1.replaceHolding(
        SpecificHolding(tsla, Custodian.Robinhood, BigDecimal.valueOf(5), now)
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