package com.rubyhuntersky.vx.dashes

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.*
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class TitleDashTest {

    private val viewId = ViewId()
    private val latitudeSubject = PublishSubject.create<Dash.Latitude>()
    private val eventSubject = PublishSubject.create<Nothing>()
    private val viewMock = mock<Dash.View<TextLineSight, Nothing>> {
        on { latitudes } doReturn latitudeSubject
        on { events } doReturn eventSubject
    }
    private val hostMock = mock<Dash.ViewHost> {
        on { addTextLine(viewId) } doReturn viewMock
    }
    private val view = TitleDash.enview(hostMock, viewId)

    @Test
    fun setSight() {
        view.setSight("Hello")
        verify(viewMock).setSight(
            TextLineSight(
                "Hello",
                TextStyle.Highlight5
            )
        )
    }

    @Test
    fun setLimit() {
        view.setHBound(HBound(0, 20))
        verify(viewMock).setHBound(HBound(0, 20))
    }

    @Test
    fun setAnchor() {
        view.setAnchor(Anchor(0, 0f))
        verify(viewMock).setAnchor(Anchor(0, 0f))
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        latitudeSubject.onNext(Dash.Latitude(100))
        test.assertValue(Dash.Latitude(100))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}