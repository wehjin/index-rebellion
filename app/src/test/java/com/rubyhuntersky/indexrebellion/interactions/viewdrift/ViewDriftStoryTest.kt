package com.rubyhuntersky.indexrebellion.interactions.viewdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.fixture.Fixture
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.Test
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.ViewDriftStory.Vision as ViewDriftVision

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
            ViewDriftVision.Idle, ViewDriftVision.Reading, ViewDriftVision.Viewing(drift)
        )

        story.sendAction(Action.ViewHolding(Fixture.TSLA_INSTRUMENT))
        ViewHoldingStory.findInEdge(edge).visions.test()
            .assertValue(
                ViewHoldingStory.Vision.Viewing(drift.findHolding(Fixture.TSLA_INSTRUMENT)!!, Plate.Unknown)
            )
    }
}