package com.rubyhuntersky.indexrebellion.spirits.readrebellion

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import org.junit.Test

class ReadRebellionDjinnTest {

    private val rebellion1 = Rebellion().setNewInvestment(CashAmount(17))
    private val rebellion2 = Rebellion().setNewInvestment(CashAmount(23))

    @Test
    fun main() {
        val book = MemoryRebellionBook()
        val djinn = ReadRebellionDjinn(book)
        val test = djinn.toObservable(ReadRebellionParams).test()
        book.write(rebellion1)
        book.write(rebellion2)
        test.assertValues(Rebellion(), rebellion1, rebellion2)
    }
}