package com.rubyhuntersky.indexrebellion.interactions.correctiondetails

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.indexrebellion.data.assets.OwnedAsset
import com.rubyhuntersky.indexrebellion.data.assets.PriceSample
import com.rubyhuntersky.indexrebellion.data.assets.ShareCount
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import com.rubyhuntersky.interaction.core.SwitchWell
import org.junit.Test
import java.util.*

class CorrectionDetailsStoryTest {

    private val assetSymbol = AssetSymbol("TSL")
    private val date = Date()
    private val details = CorrectionDetails(
        assetSymbol,
        OwnedAsset(assetSymbol, ShareCount.ONE, PriceSample(CashAmount.ONE, date)),
        CashAmount.TEN
    )
    private val detailsBook = CorrectionDetailsBook(details, MemoryRebellionBook())

    private val well = SwitchWell()
    private val interaction = CorrectionDetailsStory(well)
        .also {
            val start = Action.Start(Culture(detailsBook))
            it.sendAction(start)
        }

    @Test
    fun construction() {
        interaction.visions.test()
            .assertValue(Vision.Viewing(details, Culture(detailsBook)))
    }

    @Test
    fun newDetail() {
        val newDetails = CorrectionDetails(
            assetSymbol,
            OwnedAsset(assetSymbol, ShareCount.ONE, PriceSample(CashAmount.ONE, date)),
            CashAmount.ONE
        )
        detailsBook.write(newDetails)
        interaction.visions.test()
            .assertValue(Vision.Viewing(newDetails, Culture(detailsBook)))
    }

//    @Test
//    @Ignore("Re-enable and fix after dealing with edge and well globals")
//    fun updateShares() {
//        val mockCatalyst = mock<Portal<AssetSymbol>>()
//        interaction.sendAction(Action.UpdateShares)
//        verify(mockCatalyst).jump(assetSymbol)
//    }
}