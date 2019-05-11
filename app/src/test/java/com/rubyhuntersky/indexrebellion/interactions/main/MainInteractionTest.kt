package com.rubyhuntersky.indexrebellion.interactions.main

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.Portal
import io.reactivex.Observable
import org.junit.Test

class MainInteractionTest {

    private val mockCorrectionDetailsCatalyst = mock<Portal<CorrectionDetails>> {}
    private val mockConstituentSearchCatalyst = mock<Portal<Unit>> {}
    private val mockCashEditingCatalyst = mock<Portal<Unit>> {}

    @Test
    fun startsInLoadingState() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.never()
            override fun write(value: Rebellion) = Unit
        }
        val mainInteraction = MainInteraction(
            rebellionBook,
            MainPortals(
                mockCorrectionDetailsCatalyst,
                mockConstituentSearchCatalyst,
                mockCashEditingCatalyst
            )
        )

        mainInteraction.visionStream.test()
            .assertSubscribed()
            .assertValues(Vision.Loading)
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun shiftsToViewingWhenRebellionArrives() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion>
                get() = Observable.fromArray(
                    Rebellion()
                )

            override fun write(value: Rebellion) = Unit
        }
        val mainInteraction = MainInteraction(
            rebellionBook,
            MainPortals(
                mockCorrectionDetailsCatalyst,
                mockConstituentSearchCatalyst,
                mockCashEditingCatalyst
            )
        )

        mainInteraction.visionStream.test()
            .assertSubscribed()
            .assertValue { it is Vision.Viewing }
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun findConstituentActionStartsConstituentSearchInteraction() {
        val mainInteraction = MainInteraction(
            rebellionBook = MemoryRebellionBook(),
            portals = MainPortals(
                mockCorrectionDetailsCatalyst,
                mockConstituentSearchCatalyst,
                mockCashEditingCatalyst
            )
        )

        mainInteraction.sendAction(Action.FindConstituent)
        verify(mockConstituentSearchCatalyst).jump(Unit)
    }

    @Test
    fun openCashEditorActionCatalyzesCashEditingCatalyst() {
        val mainInteraction = MainInteraction(
            rebellionBook = MemoryRebellionBook(),
            portals = MainPortals(
                correctionDetailPortal = mockCorrectionDetailsCatalyst,
                constituentSearchPortal = mockConstituentSearchCatalyst,
                cashEditingPortal = mockCashEditingCatalyst
            )
        )
        mainInteraction.sendAction(Action.OpenCashEditor)
        verify(mockCashEditingCatalyst).jump(Unit)
    }
}