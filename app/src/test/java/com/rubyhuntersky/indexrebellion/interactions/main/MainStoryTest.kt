package com.rubyhuntersky.indexrebellion.interactions.main

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.interactions.books.MemoryRebellionBook
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Portal
import io.reactivex.Observable
import org.junit.Test

class MainStoryTest {

    private val mockConstituentSearchCatalyst = mock<Portal<Unit>> {}
    private val mockCashEditingCatalyst = mock<Portal<Unit>> {}

    @Test
    fun startsInLoadingState() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion> get() = Observable.never()
            override fun write(value: Rebellion) = Unit
        }
        val mainInteraction = startMainInteraction(rebellionBook)
        mainInteraction.visions.test()
            .assertSubscribed()
            .assertValues(Vision.Loading(mainPortals, rebellionBook))
            .assertNotComplete()
            .assertNoErrors()
    }

    private val mainPortals = MainPortals(mockConstituentSearchCatalyst, mockCashEditingCatalyst)

    private fun startMainInteraction(rebellionBook: RebellionBook): MainStory {
        return MainStory()
            .also { story ->
                with(Edge()) {
                    MainStory.addSpiritsToLamp(lamp)
                    addInteraction(story)
                }
                story.sendAction(Action.Start(rebellionBook, mainPortals))
            }
    }

    @Test
    fun shiftsToViewingWhenRebellionArrives() {
        val rebellionBook = object : RebellionBook {
            override val reader: Observable<Rebellion>
                get() = Observable.fromArray(Rebellion())

            override fun write(value: Rebellion) = Unit
        }
        val mainInteraction = startMainInteraction(rebellionBook)
        mainInteraction.visions.test()
            .assertSubscribed()
            .assertValue { it is Vision.Viewing }
            .assertNotComplete()
            .assertNoErrors()
    }

    @Test
    fun findConstituentActionStartsConstituentSearchInteraction() {
        val mainInteraction = startMainInteraction(MemoryRebellionBook())
        mainInteraction.sendAction(Action.FindConstituent)
        verify(mockConstituentSearchCatalyst).jump(Unit)
    }

    @Test
    fun openCashEditorActionCatalyzesCashEditingCatalyst() {
        val mainInteraction = startMainInteraction(MemoryRebellionBook())
        mainInteraction.sendAction(Action.OpenCashEditor)
        verify(mockCashEditingCatalyst).jump(Unit)
    }
}