package com.rubyhuntersky.indexrebellion.presenters.main

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class CorrectionWeightsCalculatorTest {
    @Test
    fun highOfZeroAssignsAllWeightToSpace() {

        val result = CorrectionWeightsCalculator.calculate(
            highValue = 0.0,
            targetValue = 0.0,
            actualValue = 0.0
        )
        assertEquals(
            CorrectionWeights(1000.0, 0.0, 0.0, 0.0, 1000.0),
            result
        )
    }

    @Test
    fun equalValuesAssignsEqualWeightToWings() {
        val result = CorrectionWeightsCalculator.calculate(
            highValue = .5,
            targetValue = .25,
            actualValue = .25
        )
        assertEquals(
            CorrectionWeights(500.0, 500.0, 500.0, 0.0, 500.0),
            result
        )
    }

    @Test
    fun higherTargetSplitsLeftWingBetweenRightWingAndSpecial() {
        val result = CorrectionWeightsCalculator.calculate(
            highValue = .5,
            targetValue = .25,
            actualValue = .125
        )
        assertEquals(
            CorrectionWeights(500.0, 500.0, 250.0, 250.0, 500.0),
            result
        )
    }

    @Test
    fun lowerTargetBalancesLeftAndRightWingsWithLeftSpaceEqualToRightWingAndSpecial() {
        val result = CorrectionWeightsCalculator.calculate(
            highValue = .5,
            targetValue = .125,
            actualValue = .25
        )
        assertEquals(
            CorrectionWeights(750.0, 250.0, 250.0, 250.0, 500.0),
            result
        )
    }
}