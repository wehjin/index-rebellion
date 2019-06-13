package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrap
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapTower
import org.junit.Assert
import org.junit.Test

class TextWrapTowerTest {

    private val id = ViewId()
    private val viewMock = mock<Tower.View<TextWrap, Nothing>>()
    private val hostMock = mock<Tower.ViewHost> {
        on { addTextWrap(id) } doReturn viewMock
    }

    @Test
    fun envisionPassesIdAndReturnsHostView() {
        val tower = TextWrapTower()
        val view = tower.enview(hostMock, id)
        verify(hostMock).addTextWrap(id)
        Assert.assertEquals(viewMock, view)
    }
}