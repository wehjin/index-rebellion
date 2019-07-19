package com.rubyhuntersky.indexrebellion.interactions.updateshares

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class UpdateSharesTest {

    @Test
    fun happy() {
        val assetSymbol = AssetSymbol("AMD")
        val sharePrice = PriceSample(CashAmount.ZERO, Date(0))
        val holding = OwnedAsset(assetSymbol, ShareCount.ONE, sharePrice)
        val rebellion = Rebellion(holdings = listOf(holding).associateBy { it.assetSymbol })
        val rebellionBook = mock<RebellionBook> {
            on { value } doReturn (rebellion)
        }
        val date = Date(1000000)

        val interaction = UpdateSharesStory()
            .also {
                Edge().addInteraction(it)
            }
        interaction.visions.test()
            .assertValue {
                it is Vision.Loading
            }

        interaction.sendAction(Action.Start(rebellionBook, assetSymbol))
        interaction.visions.test()
            .assertValue(
                Vision.Prompt(
                    assetSymbol,
                    ShareCount(1.0),
                    SharesChange.None,
                    sharePrice,
                    null,
                    true,
                    rebellionBook
                )
            )

        interaction.sendAction(Action.NewSharesChange(SharesChange.Addition("9")))
        interaction.visions.test()
            .assertValue { vision ->
                (vision as? Vision.Prompt)?.let {
                    val canUpdate = it.canUpdate
                    val isAddition = it.sharesChange is SharesChange.Addition
                    canUpdate && isAddition
                } ?: false
            }

        interaction.sendAction(Action.NewPrice("1"))
        interaction.visions.test()
            .assertValue {
                it is Vision.Prompt && it.canUpdate
            }

        interaction.sendAction(Action.Save(date))
        interaction.visions.test()
            .assertValue {
                it is Vision.Dismissed
            }


        val captor = argumentCaptor<Rebellion>()
        verify(rebellionBook).write(captor.capture())
        val finalRebellion = captor.lastValue
        assertEquals(
            OwnedAsset(assetSymbol, ShareCount(10.0), PriceSample(CashAmount(1.0), date)),
            finalRebellion.findHolding(assetSymbol)
        )
        assertEquals(
            CashAmount(-9),
            finalRebellion.newInvestment
        )
    }
}