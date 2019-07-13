package com.rubyhuntersky.indexrebellion.interactions.viewplan

import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionId
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import org.junit.Test

class ViewPlanStoryTest {

    @Test
    fun story() {
        val edge = Edge().also { it.lamp.add(ReadDriftsDjinn(BehaviorBook(DEFAULT_DRIFT))) }
        val story = ViewPlanStory().also { edge.addInteraction(it) }
        story.sendAction(ViewPlanAction.Start)
        story.visions.test().assertValue {
            val plan = (it as ViewPlanVision.Viewing).plan
            val divisionIds = plan.divisions.map(Division::divisionId).toSet()
            DivisionId.values().toSet() == divisionIds
        }
    }
}