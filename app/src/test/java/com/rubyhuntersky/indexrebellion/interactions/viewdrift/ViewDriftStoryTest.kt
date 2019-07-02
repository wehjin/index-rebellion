package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.Test

class ViewDriftStoryTest {

    @Test
    fun initChangesVisionFromIdleToReadingAndViewing() {
        val edge = Edge().also {
            ViewDriftStory.addSpiritsToLamp(it.lamp, BehaviorBook(DEFAULT_DRIFT))
        }
        val story = ViewDriftStory().also {
            edge.addInteraction(it)
        }
        val test = story.visions.test()
        story.sendAction(Action.Init)
        test.assertValues(Vision.Idle, Vision.Reading, Vision.Viewing(DEFAULT_DRIFT))
    }
}