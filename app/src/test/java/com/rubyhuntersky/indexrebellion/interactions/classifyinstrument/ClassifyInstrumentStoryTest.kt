package com.rubyhuntersky.indexrebellion.interactions.classifyinstrument

import com.rubyhuntersky.indexrebellion.data.techtonic.fixture.Fixture
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.indexrebellion.spirits.genies.writeinstrumentplate.WriteInstrumentPlatingGenie
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.Test

class ClassifyInstrumentStoryTest {

    @Test
    fun story() {
        val book = BehaviorBook(Fixture.DRIFT)
        val edge = Edge().apply {
            lamp.add(ReadDriftsDjinn(book))
            lamp.add(WriteInstrumentPlatingGenie(book))
        }
        val story = ClassifyInstrumentStory().also { edge.addInteraction(it) }
        story.visions.test().assertValues(Vision.Idle)

        story.sendAction(Action.Start(Fixture.TSLA_INSTRUMENT))
        story.visions.test().assertValue(Vision.Viewing(Fixture.TSLA_INSTRUMENT, Plate.Unknown))

        story.sendAction(Action.Write(Plate.GlobalEquity))
        story.visions.test().assertValue(Vision.Viewing(Fixture.TSLA_INSTRUMENT, Plate.GlobalEquity))

        story.sendAction(Action.End)
        story.visions.test().assertValues(Vision.Ended)
    }
}