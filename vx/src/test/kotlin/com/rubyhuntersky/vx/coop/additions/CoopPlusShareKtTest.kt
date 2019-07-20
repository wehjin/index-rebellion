package com.rubyhuntersky.vx.coop.additions

import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.coop.coops.FitTextCoop
import com.rubyhuntersky.vx.coop.tools.TestCoopViewHost
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import org.junit.Assert.assertEquals
import org.junit.Test

class CoopPlusShareKtTest {

    private val viewHost = TestCoopViewHost()

    @Test
    fun testPlusShareHEnd() {
        val coop = FitTextCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.HEnd,
                    span = Span.Relative(0.25f),
                    coop = FitTextCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 75, 0, 100), BiBound(75, 100, 0, 100)),
            viewHost.items.map { it.maybeBound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.maybeSight }
        )
    }

    @Test
    fun testPlusShareHStart() {
        val coop = FitTextCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.HStart,
                    span = Span.Relative(0.25f),
                    coop = FitTextCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 25, 0, 100), BiBound(25, 100, 0, 100)),
            viewHost.items.map { it.maybeBound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.maybeSight }
        )
    }

    @Test
    fun testPlusShareVFloor() {
        val coop = FitTextCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.VFloor,
                    span = Span.Relative(0.25f),
                    coop = FitTextCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 100, 0, 75), BiBound(0, 100, 75, 100)),
            viewHost.items.map { it.maybeBound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.maybeSight }
        )
    }

    @Test
    fun testPlusShareVCeiling() {
        val coop = FitTextCoop(TextStyle.Body1, BiOrbit.StartCenterLit) +
                Share(
                    type = ShareType.VCeiling,
                    span = Span.Relative(0.25f),
                    coop = FitTextCoop(TextStyle.Body1, BiOrbit.EndCenterLit)
                )
        with(coop.enview(viewHost, ViewId())) {
            setSight("Hello")
            setBound(BiBound(0, 100, 0, 100))
        }

        assertEquals(
            setOf(BiBound(0, 100, 0, 25), BiBound(0, 100, 25, 100)),
            viewHost.items.map { it.maybeBound }.toSet()
        )
        assertEquals(
            listOf("Hello", "Hello"),
            viewHost.items.map { it.maybeSight }
        )
    }
}