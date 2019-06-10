package com.rubyhuntersky.vx.additions

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.dashes.TextLineSight
import com.rubyhuntersky.vx.dashes.TextStyle
import com.rubyhuntersky.vx.dashes.TitleDash
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class BottomTest {
    private val viewId = ViewId()

    private val latitudeSubjectA = PublishSubject.create<Dash.Latitude>()
    private val eventSubjectA = PublishSubject.create<Nothing>()
    private val viewMockA = mock<Dash.View<TextLineSight, Nothing>> {
        on { latitudes } doReturn latitudeSubjectA
        on { events } doReturn eventSubjectA
    }

    private val latitudeSubjectB = PublishSubject.create<Dash.Latitude>()
    private val eventSubjectB = PublishSubject.create<Nothing>()
    private val viewMockB = mock<Dash.View<TextLineSight, Nothing>> {
        on { latitudes } doReturn latitudeSubjectB
        on { events } doReturn eventSubjectB
    }

    private val hostMock = mock<Dash.ViewHost> {
        on { addTextLine(ViewId().extend(0)) } doReturn viewMockA
        on { addTextLine(ViewId().extend(1)) } doReturn viewMockB
    }
    private val dash = TitleDash + Bottom(TitleDash) { sight: Pair<String, String> -> sight }
    private val view = dash.enview(hostMock, viewId)

    @Test
    fun setSight() {
        view.setSight(Pair("Hello", "World"))
        verify(viewMockA).setSight(
            TextLineSight(
                "Hello",
                TextStyle.Highlight5
            )
        )
        verify(viewMockB).setSight(
            TextLineSight(
                "World",
                TextStyle.Highlight5
            )
        )
    }

    @Test
    fun setLimit() {
        val limit = HBound(0, 20)
        view.setHBound(limit)
        verify(viewMockA).setHBound(limit)
        verify(viewMockB).setHBound(limit)
    }

    @Test
    fun setAnchor() {
        latitudeSubjectA.onNext(Dash.Latitude(75))
        latitudeSubjectB.onNext(Dash.Latitude(25))
        view.setAnchor(Anchor(0, 0f))
        verify(viewMockA).setAnchor(Anchor(0, 0f))
        verify(viewMockB).setAnchor(Anchor(100, 1f))
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        latitudeSubjectA.onNext(Dash.Latitude(100))
        latitudeSubjectB.onNext(Dash.Latitude(5))
        test.assertValue(Dash.Latitude(105))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}