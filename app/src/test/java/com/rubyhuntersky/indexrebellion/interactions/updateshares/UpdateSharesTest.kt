package com.rubyhuntersky.indexrebellion.interactions.updateshares

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionHoldingBook
import org.junit.Test
import java.util.*

class UpdateSharesTest {

    @Test
    fun happy() {
        val rebellionBook = mock<RebellionBook>()
        val assetSymbol = AssetSymbol("AMD")
        val holdingBook = RebellionHoldingBook(rebellionBook, assetSymbol)

        val interaction = UpdateShares.Interaction(holdingBook)
        interaction.visions.test()
            .assertValue {
                it is UpdateShares.Vision.Loading
            }

        val holding = OwnedAsset(assetSymbol, ShareCount.ONE, PriceSample(CashAmount.ZERO, Date(0)))
        interaction.sendAction(UpdateShares.Action.Load(holding))
        interaction.visions.test()
            .assertValue {
                it is UpdateShares.Vision.Prompt && !it.canUpdate && it.numberDelta is UpdateShares.NumberDelta.Undecided && it.ownedCount == 1
            }

        interaction.sendAction(UpdateShares.Action.NewChangeCount("9"))
        interaction.visions.test()
            .assertValue {
                it is UpdateShares.Vision.Prompt && !it.canUpdate && it.numberDelta is UpdateShares.NumberDelta.Change
            }

        interaction.sendAction(UpdateShares.Action.NewPrice("1"))
        interaction.visions.test()
            .assertValue {
                it is UpdateShares.Vision.Prompt && it.canUpdate
            }

        val date = Date(1000000)
        interaction.sendAction(UpdateShares.Action.Save(date))
        interaction.visions.test()
            .assertValue {
                it is UpdateShares.Vision.Dismissed
            }

        verify(rebellionBook).updateShareCountPriceAndCash(
            assetSymbol, ShareCount.TEN, PriceSample(CashAmount.ONE, date), CashAmount(-9)
        )
    }
}