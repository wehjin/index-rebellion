package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.tower.Tower
import org.junit.Assert
import org.junit.Test

class TextLineTowerTest {

    private val id = ViewId()
    private val viewMock = mock<Tower.View<TextLineSight, Nothing>>()
    private val hostMock = mock<Tower.ViewHost> {
        on { addTextLine(id) } doReturn viewMock
    }

    @Test
    fun envisionPassesIdAndReturnsHostView() {
        val tower = TextLineTower()
        val view = tower.enview(hostMock, id)
        verify(hostMock).addTextLine(id)
        Assert.assertEquals(viewMock, view)
    }
}