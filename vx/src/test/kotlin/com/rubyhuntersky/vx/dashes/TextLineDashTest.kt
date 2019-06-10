package com.rubyhuntersky.vx.dashes

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.ViewId
import org.junit.Assert
import org.junit.Test

class TextLineDashTest {

    private val id = ViewId()
    private val viewMock = mock<Dash.View<TextLineSight, Nothing>>()
    private val hostMock = mock<Dash.ViewHost> {
        on { addTextLine(id) } doReturn viewMock
    }

    @Test
    fun envisionPassesIdAndReturnsHostView() {
        val dash = TextLineDash()
        val view = dash.enview(hostMock, id)
        verify(hostMock).addTextLine(id)
        Assert.assertEquals(viewMock, view)
    }
}