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
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object ChooseHoldingTypeStoryTest : Spek({

    fun Edge.assertHoldingEditStoryIsEditingWithType(holdingEditType: HoldingEditType) {
        findInteraction<EditHoldingStory.Vision, EditHoldingStory.Action>(EditHoldingStory.searchByGroup)
            .visions.test()
            .assertValue {
                val editingVision = it as EditHoldingStory.Vision.Editing
                editingVision.holdingEdit.type == holdingEditType
            }
    }

    describe("ChooseHoldingType story") {
        it("begins with choices") {
            val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
            val story = ChooseHoldingTypeStory().also { edge.addInteraction(it) }
            story.visions.test().assertValue(Vision.Choosing(ChooseHoldingTypeStory.DEFAULT_CHOICES))
        }

        it("launches EditHoldingStory with type Stock when choice is STOCKS") {
            val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
            val story = ChooseHoldingTypeStory().also { edge.addInteraction(it) }
            story.sendAction(Action.Choose(HoldingType.STOCKS))
            story.visions.test().assertValue(Vision.ChoiceMade(HoldingType.STOCKS))
            edge.assertHoldingEditStoryIsEditingWithType(HoldingEditType.Stock)
        }

        it("launches EditHoldingStory with type FixedInstrument when choice is DOLLARS") {
            val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
            val story = ChooseHoldingTypeStory().also { edge.addInteraction(it) }
            story.sendAction(Action.Choose(HoldingType.DOLLARS))
            story.visions.test().assertValue(Vision.ChoiceMade(HoldingType.DOLLARS))
            edge.assertHoldingEditStoryIsEditingWithType(HoldingEditType.FixedInstrument(DOLLAR_INSTRUMENT))
        }
    }
})

