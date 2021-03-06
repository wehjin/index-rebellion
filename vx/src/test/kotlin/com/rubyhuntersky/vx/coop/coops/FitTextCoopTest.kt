package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.bound.VBound
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FitTextCoopTest {

    private val viewHost = TestCoopViewHost()
    private val frameBound = BiBound(HBound(0, 10), VBound(0, 20))
    private val id = ViewId()
    private val text = "Hello"
    private val textStyle = TextStyle.Body1
    private val orbit = BiOrbit.StartCenterLit

    @Test
    fun testBoundSightStyle() {
        val coop = FitTextCoop(textStyle, orbit)
        with(coop.enview(viewHost, id)) {
            setBound(frameBound)
            setSight(text)
        }
        when (val item = viewHost.items.first()) {
            is TestCoopViewHost.Item.FITTEXT -> {
                assertEquals(frameBound, item.maybeBound)
                assertEquals(text, item.maybeSight)
                assertEquals(id, item.id)
                assertEquals(textStyle, item.textStyle)
                assertEquals(orbit, item.orbit)
            }
            else -> error("Not FITTEXT")
        }
    }
}

