package com.rubyhuntersky.vx.tower.additions

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.TitleTower
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class BottomTest {
    private val viewId = ViewId()

    private val latitudeSubjectA = PublishSubject.create<Latitude>()
    private val eventSubjectA = PublishSubject.create<Nothing>()
    private val viewMockA = mock<Tower.View<WrapTextSight, Nothing>> {
        on { latitudes } doReturn latitudeSubjectA
        on { events } doReturn eventSubjectA
    }

    private val latitudeSubjectB = PublishSubject.create<Latitude>()
    private val eventSubjectB = PublishSubject.create<Nothing>()
    private val viewMockB = mock<Tower.View<WrapTextSight, Nothing>> {
        on { latitudes } doReturn latitudeSubjectB
        on { events } doReturn eventSubjectB
    }

    private val hostMock = mock<Tower.ViewHost> {
        on { addWrapTextView(ViewId().extend(0)) } doReturn viewMockA
        on { addWrapTextView(ViewId().extend(1)) } doReturn viewMockB
    }
    private val tower = TitleTower + Bottom(TitleTower) { sight: Pair<String, String> -> sight }
    private val view = tower.enview(hostMock, viewId)

    @Test
    fun setSight() {
        view.setSight(Pair("Hello", "World"))
        verify(viewMockA).setSight(
            WrapTextSight(
                "Hello",
                TextStyle.Highlight5
            )
        )
        verify(viewMockB).setSight(
            WrapTextSight(
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
        latitudeSubjectA.onNext(Latitude(75))
        latitudeSubjectB.onNext(Latitude(25))
        view.setAnchor(Anchor(0, 0f))
        verify(viewMockA).setAnchor(Anchor(0, 0f))
        verify(viewMockB).setAnchor(Anchor(100, 1f))
    }


    @Test
    fun latitudes() {
        val test = view.latitudes.test()
        latitudeSubjectA.onNext(Latitude(100))
        latitudeSubjectB.onNext(Latitude(5))
        test.assertValue(Latitude(105))
    }

    @Test
    fun events() {
        view.events.test().assertNoValues()
    }
}