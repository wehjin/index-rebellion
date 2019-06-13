package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrap
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class TitleTowerTest {

    private val viewId = ViewId()
    private val latitudeSubject = PublishSubject.create<Tower.Latitude>()
    private val eventSubject = PublishSubject.create<Nothing>()
    private val viewMock = mock<Tower.View<TextWrap, Nothing>> {
        on { latitudes } doReturn latitudeSubject
        on { events } doReturn eventSubject
    }
    private val hostMock = mock<Tower.ViewHost> {
        on { addTextWrap(viewId) } doReturn viewMock
    }
    private val view = TitleTower.enview(hostMock, viewId)

    @Test
    fun setSight() {
        view.setSight("Hello")
        verify(viewMock).setSight(
            TextWrap(
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
        latitudeSubject.onNext(Tower.Latitude(100))
        test.assertValue(Tower.Latitude(100))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}