package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.coops.SingleTextLineCoop
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

class CoopPlusShareKtTest {

    private val viewHost = TestCoopViewHost()

    @Test
    fun testPlusShareHEnd() {
        val coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.HEnd,
                    span = Span.Relative(0.25f),
                    coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 75, 0, 100), BiBound(75, 100, 0, 100)),
            viewHost.items.map { it.bound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.sight }
        )
    }

    @Test
    fun testPlusShareHStart() {
        val coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.HStart,
                    span = Span.Relative(0.25f),
                    coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 25, 0, 100), BiBound(25, 100, 0, 100)),
            viewHost.items.map { it.bound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.sight }
        )
    }

    @Test
    fun testPlusShareVFloor() {
        val coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.VFloor,
                    span = Span.Relative(0.25f),
                    coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 100, 0, 75), BiBound(0, 100, 75, 100)),
            viewHost.items.map { it.bound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.sight }
        )
    }

    @Test
    fun testPlusShareVCeiling() {
        val coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.VCeiling,
                    span = Span.Relative(0.25f),
                    coop = SingleTextLineCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 100, 0, 25), BiBound(0, 100, 25, 100)),
            viewHost.items.map { it.bound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.sight }
        )
    }
}