package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.fixture.Fixture
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.Test
import com.rubyhuntersky.indexrebellion.interactions.viewholding.Vision as ViewHoldingVision

class ViewDriftStoryTest {

    private val drift = Fixture.DRIFT

    private val edge = Edge()
        .apply { lamp.add(ReadDriftsDjinn(BehaviorBook(drift))) }

    private val story = ViewDriftStory()
        .also { edge.addInteraction(it) }

    private val test = story.visions.test()

    @Test
    fun story() {
        story.sendAction(Action.Init)
        test.assertValues(
            Vision.Idle, Vision.Reading, Vision.Viewing(drift)
        )

        story.sendAction(Action.ViewHolding(Fixture.TSLA_INSTRUMENT))
        ViewHoldingStory.findInEdge(edge).visions.test()
            .assertValue(
                ViewHoldingVision.Viewing(drift.findHolding(Fixture.TSLA_INSTRUMENT)!!, Plate.Unknown)
            )
    }
}