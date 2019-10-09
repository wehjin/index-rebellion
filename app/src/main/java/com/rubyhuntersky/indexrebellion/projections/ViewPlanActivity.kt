package com.rubyhuntersky.indexrebellion.projections

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElement
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewplan.ViewPlanStory.Vision
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.android.tower.TowerActivity
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.clicks.plusClicks
import com.rubyhuntersky.vx.tower.additions.logAnchors
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.additions.plusHMargin
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.additions.shareEnd

class ViewPlanActivity : TowerActivity<Vision, Nothing>() {

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

    private val divisionNameTower = Standard.TitleTower()
        .mapSight { division: Division ->
            division.divisionId.name
        }
        .logAnchors("TitleTower")

    private val divisionTower = divisionNameTower
        .extendFloor(elementsTower.mapSight { division: Division -> division.divisionElements }.neverEvent())
        .plusHMargin(Standard.uniformMargin).plusVPad(Standard.uniformPad)
        .plusClicks { interaction.sendAction(Action.ViewDivision(it.divisionId)) }

    private val visionTower = divisionTower.replicate()
        .mapSight { vision: Vision ->
            val viewing = vision as? Vision.Viewing
            viewing?.plan?.divisions ?: listOf(Division.EMPTY)
        }
        .neverEvent<Nothing>()

    override val activityTower: Tower<Vision, Nothing> = visionTower

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interaction = ActivityInteraction(group, this, this::renderVision).also { lifecycle.addObserver(it) }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun renderVision(vision: Vision, sendAction: (Action) -> Unit, edge: Edge) = vx.setSight(vision)

    override fun onBackPressed() = interaction.sendAction(Action.End)

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