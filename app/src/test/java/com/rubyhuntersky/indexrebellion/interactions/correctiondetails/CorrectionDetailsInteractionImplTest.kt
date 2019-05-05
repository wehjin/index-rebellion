package com.rubyhuntersky.indexrebellion.interactions.correctiondetails

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.core.Portal
import org.junit.Test

class CorrectionDetailsInteractionImplTest {

    private val assetSymbol = AssetSymbol("TSL")
    private val details = CorrectionDetails(
        assetSymbol,
        ShareCount.ONE,
        CashAmount.ONE,
        CashAmount.TEN
    )
    private val mockCatalyst = mock<Portal<AssetSymbol>>()
    private val saverBook = CorrectionDetailsBook(details, MemoryRebellionBook())

    private val interaction = CorrectionDetailsInteractionImpl(saverBook, mockCatalyst)

    @Test
    fun construction() {
        interaction.visionStream.test()
            .assertValue(Vision.Viewing(details))
    }

    @Test
    fun newDetail() {
        val newDetails = CorrectionDetails(
            assetSymbol,
            ShareCount.ONE,
            CashAmount.ONE,
            CashAmount.ONE
        )
        saverBook.write(newDetails)
        interaction.visionStream.test()
            .assertValue(Vision.Viewing(newDetails))
    }

    @Test
    fun updateShares() {
        interaction.sendAction(Action.UpdateShares)
        verify(mockCatalyst).jump(assetSymbol)
    }
}