package com.rubyhuntersky.indexrebellion.interactions.cashediting

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CashEditingTest {

    private val rebellionBook = MemoryRebellionBook()
    private val interaction = CashEditing(rebellionBook)

    @Test
    fun happy() {
        interaction.visions.test().assertValue(Vision.Idle)

        interaction.sendAction(Action.Load)
        interaction.visions.test().assertValue(Vision.Editing(CashAmount.ZERO, "", false))

        interaction.sendAction(Action.SetEdit("3.00"))
        interaction.visions.test().assertValue(Vision.Editing(CashAmount.ZERO, "3.00", true))

        interaction.sendAction(Action.Save)
        interaction.visions.test().assertValue(Vision.Idle)
        assertEquals(3.00, rebellionBook.value.newInvestment.toDouble(), 0.01)
    }
}