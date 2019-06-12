package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bounds.BiBound
import com.rubyhuntersky.vx.bounds.HBound
import com.rubyhuntersky.vx.bounds.VBound
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import org.junit.Assert.assertEquals
import org.junit.Test

class SingleTextLineCoopTest {

    private val viewHost = TestCoopViewHost()
    private val frameBound = BiBound(HBound(0, 10), VBound(0, 20))
    private val id = ViewId()
    private val textStyle = TextStyle.Body1
    private val text = "Hello"

    @Test
    fun testBoundSightStyle() {
        val coop = SingleTextLineCoop(textStyle)
        with(coop.enview(viewHost, id)) {
            setBound(frameBound)
            setSight(text)
        }
        assertEquals(
            TestCoopViewHost.Item.SingleTextLine(id, frameBound, text, textStyle),
            viewHost.items.first()
        )
    }
}

