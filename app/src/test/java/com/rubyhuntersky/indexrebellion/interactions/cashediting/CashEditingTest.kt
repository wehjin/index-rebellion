package com.rubyhuntersky.indexrebellion.interactions.cashediting

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import org.junit.Assert.assertEquals
import org.junit.Test

class CashEditingTest {

    private val rebellionBook = MemoryRebellionBook()
    private val interaction = CashEditing(rebellionBook)

    @Test
    fun happy() {
        interaction.visionStream.test().assertValue(Vision.Idle)

        interaction.reset()
        interaction.visionStream.test().assertValue(Vision.Editing(CashAmount.ZERO, "", false))

        interaction.sendAction(Action.SetEdit("3.00"))
        interaction.visionStream.test().assertValue(Vision.Editing(CashAmount.ZERO, "3.00", true))

        interaction.sendAction(Action.Save)
        interaction.visionStream.test().assertValue(Vision.Idle)
        assertEquals(3.00, rebellionBook.value.newInvestment.toDouble(), 0.01)
    }
}