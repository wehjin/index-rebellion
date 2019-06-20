package com.rubyhuntersky.indexrebellion.interactions.holdings

import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.Test

class HoldingsStoryTest {

    @Test
    fun initChangesVisionFromIdleToReadingAndViewing() {
        val edge = Edge().also {
            HoldingsStory.addSpiritsToLamp(it.lamp, BehaviorBook(DEFAULT_DRIFT))
        }
        val story = HoldingsStory().also {
            edge.addInteraction(it)
        }
        val test = story.visions.test()
        story.sendAction(Action.Init)
        test.assertValues(Vision.Idle, Vision.Reading, Vision.Viewing(DEFAULT_DRIFT))
    }
}