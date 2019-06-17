package com.rubyhuntersky.vx.tower.towers

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextTower
import org.junit.Assert
import org.junit.Test

class WrapTextTowerTest {

    private val id = ViewId()
    private val viewMock = mock<Tower.View<WrapTextSight, Nothing>>()
    private val hostMock = mock<Tower.ViewHost> {
        on { addTextWrap(id) } doReturn viewMock
    }

    @Test
    fun envisionPassesIdAndReturnsHostView() {
        val tower = WrapTextTower()
        val view = tower.enview(hostMock, id)
        verify(hostMock).addTextWrap(id)
        Assert.assertEquals(viewMock, view)
    }
}