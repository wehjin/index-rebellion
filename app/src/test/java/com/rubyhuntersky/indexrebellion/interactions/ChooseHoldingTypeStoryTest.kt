package com.rubyhuntersky.indexrebellion.interactions

import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.DOLLAR_INSTRUMENT
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory.Action
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory.Vision
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.editholding.HoldingEditType
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.jupiter.api.Test


class ChooseHoldingTypeStoryTest {

    @Test
    fun beginsWithChoices() {
        val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
        val story = ChooseHoldingTypeStory().also { edge.addInteraction(it) }
        story.visions.test().assertValue(Vision.Choosing(ChooseHoldingTypeStory.DEFAULT_CHOICES))
    }

    @Test
    fun launchesEditHoldingStoryWithTypeStockWhenChoiceIsSTOCKS() {
        val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
        val story = ChooseHoldingTypeStory().also { edge.addInteraction(it) }
        story.sendAction(Action.Choose(HoldingType.STOCKS))
        story.visions.test().assertValue(Vision.ChoiceMade(HoldingType.STOCKS))
        edge.assertHoldingEditStoryIsEditingWithType(HoldingEditType.Stock)
    }

    private fun Edge.assertHoldingEditStoryIsEditingWithType(holdingEditType: HoldingEditType) {
        findInteraction<EditHoldingStory.Vision, EditHoldingStory.Action>(EditHoldingStory.searchByGroup)
            .visions.test()
            .assertValue {
                val editingVision = it as EditHoldingStory.Vision.Editing
                editingVision.holdingEdit.type == holdingEditType
            }
    }

    @Test
    fun launchesEditHoldingStoryWithTypeFixedInstrumentWhenChoiceIsDOLLARS() {
        val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
        val story = ChooseHoldingTypeStory().also { edge.addInteraction(it) }
        story.sendAction(Action.Choose(HoldingType.DOLLARS))
        story.visions.test().assertValue(Vision.ChoiceMade(HoldingType.DOLLARS))
        edge.assertHoldingEditStoryIsEditingWithType(HoldingEditType.FixedInstrument(DOLLAR_INSTRUMENT))
    }
}

