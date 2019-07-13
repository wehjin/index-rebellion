package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElement
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.coop.additions.Span
import com.rubyhuntersky.vx.tower.additions.*
import com.rubyhuntersky.vx.tower.additions.augment.extendFloor
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanAction as Action
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanVision as Vision

class ViewPlanActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val elementsTower = Standard.ItemAttributeTower()
        .mapSight { sight: List<DivisionElement> -> asRange(sight, 0) }
        .shareEnd(
            span = Span.Relative(1f / 2f),
            tower = Standard.ItemAttributeTower(Orbit.Center)
                .mapSight { sight: List<DivisionElement> -> asRange(sight, 2) }
        )
        .shareEnd(
            span = Span.Relative(1f / 3f),
            tower = Standard.ItemAttributeTower(Orbit.TailLit)
                .mapSight { sight: List<DivisionElement> -> asRange(sight, 1) }
        )

    private val titleTower = Standard.TitleTower()
        .mapSight { division: Division ->
            division.divisionId.name
        }
        .logAnchors("TitleTower")

    private val divisionTower = titleTower
        .extendFloor(elementsTower.mapSight { division: Division -> division.divisionElements }.neverEvent())
        .plusHMargin(Standard.uniformMargin).plusVPad(Standard.uniformPad)

    private val tower = divisionTower.replicate()
        .mapSight { vision: Vision ->
            val viewing = vision as? Vision.Viewing
            viewing?.plan?.divisions ?: listOf(Division.EMPTY)
        }
        .neverEvent<Nothing>()

    private val coopContentView = CoopContentView(tower.inCoop())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityInteraction()
        coopContentView.setInActivity(this)
    }

    private fun startActivityInteraction() {
        val activityInteraction = ActivityInteraction(group, this, this::renderVision)
        lifecycle.addObserver(activityInteraction)
        interaction = activityInteraction
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) {
        coopContentView.setSight(vision)
    }

    override fun onBackPressed() {
        interaction.sendAction(Action.End)
    }

    companion object : ProjectionSource<Vision, Action> {
        override val group: String = ViewPlanStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            Intent(activity, ViewPlanActivity::class.java)
                .putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }

        private fun asRange(divisionElements: List<DivisionElement>, elementIndex: Int): ClosedRange<String> =
            divisionElements.getOrNull(elementIndex)
                ?.let { it.id.shortName.."Weight: ${it.weight.value}" }
                ?: ""..""
    }
}