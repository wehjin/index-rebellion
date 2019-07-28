package com.rubyhuntersky.indexrebellion.interactions

import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionId
import com.rubyhuntersky.indexrebellion.edit.DivisionEdit
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory.Action
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory.Vision
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.edit.Ancient
import org.junit.jupiter.api.Test

internal class EditDivisionStoryTest {

    @Test
    internal fun loadsDriftAndViewDivisionWhenStarted() {
        val initialDrift = DEFAULT_DRIFT
        val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(initialDrift))) }
        val story = EditDivisionStory().also { edge.addInteraction(it) }
        story.sendAction(Action.Start(DivisionId.Securities))
        story.visions.test().assertValue(
            Vision.Editing(DivisionEdit(Ancient(initialDrift.find(DivisionId.Securities)!!)))
        )
    }
}