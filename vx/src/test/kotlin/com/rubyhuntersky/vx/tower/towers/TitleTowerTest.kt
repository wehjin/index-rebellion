package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class TitleTowerTest {

    private val viewId = ViewId()
    private val latitudeSubject = PublishSubject.create<Latitude>()
    private val eventSubject = PublishSubject.create<Nothing>()
    private val viewMock = mock<Tower.View<WrapTextSight, Nothing>> {
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
            WrapTextSight(
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
        latitudeSubject.onNext(Latitude(100))
        test.assertValue(Latitude(100))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}