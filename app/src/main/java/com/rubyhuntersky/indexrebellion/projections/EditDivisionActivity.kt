package com.rubyhuntersky.indexrebellion.projections

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElement
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElementId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.PortfolioPlan
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Weight
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory.Action
import com.rubyhuntersky.indexrebellion.interactions.EditDivisionStory.Vision
import com.rubyhuntersky.interaction.edit.toStringChange
import com.rubyhuntersky.interaction.preandroid.ActivityProjectionSource
import com.rubyhuntersky.interaction.preandroid.InteractionTowerActivity
import com.rubyhuntersky.interaction.stringedit.StringEdit
import com.rubyhuntersky.vx.android.toTextInputSight
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.extend.extendFloor
import com.rubyhuntersky.vx.tower.additions.handleEvents
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.towers.InputType
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight


class EditDivisionActivity : InteractionTowerActivity<Vision, Action>(
    interactionName = EditDivisionStory.groupId,
    backAction = Action.End
) {

    private val elementTower =
        Standard.InsetTextInputTower<DivisionElementId>()
            .mapSight { (id, edit): Map.Entry<DivisionElementId, StringEdit<DivisionElement>> ->
                edit.toTextInputSight(InputType.UNSIGNED_DECIMAL, id, { it.weight.value.toPlainString() })
            }

    private val firstElementTower = elementTower
        .mapSight { vision: Vision ->
            when (vision) {
                is Vision.Editing -> vision.divisionEdit.subeditList.first()
                else -> {
                    val plan = PortfolioPlan(Weight.ZERO, Weight.ZERO)
                    val stringEdit = StringEdit<DivisionElement>("placeholder")
                    mapOf(Pair(plan.divisionElements.first().id, stringEdit)).entries.first()
                }
            }
        }
        .handleEvents { event ->
            val id = event.topic
            event.toStringChange()?.let {
                interaction.sendAction(
                    Action.ChangeElement(ElementChange(id, it))
                )
            }
        }

    private val elementsTower =
        elementTower.replicate()
            .mapSight { vision: Vision ->
                when (vision) {
                    is Vision.Editing -> vision.divisionEdit.subeditList
                    else -> emptyList()
                }
            }
            .handleEvents { (event, _) ->
                val id = event.topic
                event.toStringChange()?.let {
                    interaction.sendAction(
                        Action.ChangeElement(ElementChange(id, it))
                    )
                }
            }

    private val saveTower =
        Standard.CenteredTextButton<Unit>()
            .mapSight { vision: Vision ->
                (vision as? Vision.Editing)?.divisionEdit?.writableValue?.let { ButtonSight(Unit, "Save") }
                    ?: ButtonSight(Unit, "Invalid")
            }
            .handleEvents {
                interaction.sendAction(Action.Save)
            }

    private val basicVisionTower: Tower<Vision, Nothing> = Standard.BodyTower().mapSight(Any::toString)

    private val visionTower = elementsTower.extendFloor(saveTower).extendFloor(basicVisionTower)

    override val activityTower: Tower<Vision, Nothing> = visionTower

    companion object :
        ActivityProjectionSource<Vision, Action, EditDivisionActivity> {
        override val activityClass: Class<EditDivisionActivity> = EditDivisionActivity::class.java
        override val interactionName: String = EditDivisionStory.groupId
    }
}
