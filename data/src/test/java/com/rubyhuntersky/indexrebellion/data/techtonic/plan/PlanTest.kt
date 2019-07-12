package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_PLAN
import org.junit.Assert.assertEquals
import org.junit.Test

class PlanTest {

    @Test
    fun divisions() {
        val divisions = DEFAULT_PLAN.divisions
        assertEquals(DivisionId.values().toSet(), divisions.map { it.divisionId }.toSet())
    }
}