package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bound.BiBound
import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.bound.VBound
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

class SingleTextLineCoopTest {

    private val viewHost = TestCoopViewHost()
    private val frameBound = BiBound(HBound(0, 10), VBound(0, 20))
    private val id = ViewId()
    private val text = "Hello"
    private val textStyle = TextStyle.Body1
    private val orbit = BiOrbit.StartCenterLit

    @Test
    fun testBoundSightStyle() {
        val coop = SingleTextLineCoop(textStyle, orbit)
        with(coop.enview(viewHost, id)) {
            setBound(frameBound)
            setSight(text)
        }
        assertEquals(
            TestCoopViewHost.Item.SingleTextLine(id, frameBound, text, textStyle, orbit),
            viewHost.items.first()
        )
    }
}

