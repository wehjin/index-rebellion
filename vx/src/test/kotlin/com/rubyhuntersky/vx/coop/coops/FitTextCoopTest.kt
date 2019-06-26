package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.bound.VBound
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

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
        assertEquals(
            TestCoopViewHost.Item.TestFitText(id, frameBound, text, textStyle, orbit),
            viewHost.items.first()
        )
    }
}

