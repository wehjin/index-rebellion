package com.rubyhuntersky.indexrebellion.projections

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.CustodianAccount
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory.Action
import com.rubyhuntersky.indexrebellion.interactions.editholding.EditHoldingStory.Vision
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.coop.CoopContentView
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey
import com.rubyhuntersky.vx.android.toTextInputSight
import com.rubyhuntersky.vx.android.toUnit
import com.rubyhuntersky.vx.tower.additions.extend.extendFloors
import com.rubyhuntersky.vx.tower.additions.fixSight
import com.rubyhuntersky.vx.tower.additions.handleEvents
import com.rubyhuntersky.vx.tower.additions.inCoop
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.additions.pad.plusVPad
import com.rubyhuntersky.vx.tower.towerOf
import com.rubyhuntersky.vx.tower.towers.InputType
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import java.math.BigDecimal

@SuppressLint("Registered")
class EditHoldingActivity : AppCompatActivity() {

    private lateinit var interaction: Interaction<Vision, Action>

    private val symbolInputTower = Standard.InsetTextInputTower<Unit>()
        .mapSight { vision: Vision ->
            vision.symbolEdit
                ?.toTextInputSight(InputType.WORD, Unit, String::toString)
                ?: TextInputSight(
                    type = InputType.WORD,
                    topic = Unit,
                    text = "",
                    error = "BAD: $vision",
                    enabled = false
                )
        }
        .handleEvents { event ->
            (event as TextInputEvent.Changed)
                .let { Action.SetSymbol(Pair(event.text, event.selection)) }
                .let(interaction::sendAction)
        }

    private val sizeInputTower = Standard.InsetTextInputTower<Unit>()
        .mapSight { vision: Vision ->
            vision.sizeEdit
                ?.toTextInputSight(InputType.UNSIGNED_DECIMAL, Unit, BigDecimal::toString)
                ?: TextInputSight(
                    InputType.UNSIGNED_DECIMAL,
                    topic = Unit,
                    text = "",
                    error = "BAD: $vision",
                    enabled = false
                )
        }
        .handleEvents { event ->
            (event as TextInputEvent.Changed)
                .let { Action.SetSize(Pair(event.text, event.selection)) }
                .let(interaction::sendAction)
        }

    private val priceInputTower = Standard.InsetTextInputTower<Unit>()
        .mapSight { vision: Vision ->
            vision.priceEdit?.toTextInputSight(
                type = InputType.UNSIGNED_DECIMAL,
                topic = Unit,
                stringify = CashAmount::toDollarStat
            ) ?: TextInputSight(
                type = InputType.UNSIGNED_DECIMAL,
                topic = Unit,
                text = "",
                error = "BAD: $vision",
                enabled = false
            )
        }
        .handleEvents { event ->
            (event as TextInputEvent.Changed)
                .let { Action.SetPrice(Pair(event.text, event.selection)) }
                .let(interaction::sendAction)
        }

    private fun visionToAccountEdit(vision: Vision): TextInputSight<Unit> =
        vision.accountEdit?.toTextInputSight(
            type = InputType.WORD,
            topic = Unit,
            stringify = CustodianAccount::id
        ) ?: TextInputSight(
            type = InputType.WORD,
            topic = Unit,
            text = "",
            error = "BAD: $vision",
            enabled = false
        )

    private val accountInputTower =
        towerOf(this::visionToAccountEdit, Standard.InsetTextInputTower<Unit>())
            .handleEvents { event ->
                (event as TextInputEvent.Changed)
                    .let { Action.SetAccount(Pair(event.text, event.selection)) }
                    .let(interaction::sendAction)
            }

    private val saveTower = Standard.CenteredTextButton<Unit>()
        .fixSight(ButtonSight(Unit, "Save"))
        .mapSight(Vision::toUnit)
        .handleEvents {
            (it as ClickEvent.Single)
                .let { Action.Write }
                .let(interaction::sendAction)
        }

    private val pageTower = symbolInputTower
        .extendFloors(
            sizeInputTower,
            priceInputTower,
            accountInputTower,
            saveTower
        )
        .plusVPad(Standard.uniformPad)

    private val coopContentView = CoopContentView(pageTower.inCoop())

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
        override val group: String = EditHoldingStory.groupId

        override fun startProjection(
            activity: FragmentActivity,
            interaction: Interaction<Vision, Action>,
            key: Long
        ) {
            Intent(activity, EditHoldingActivity::class.java)
                .putActivityInteractionSearchKey(key)
                .let(activity::startActivity)
        }
    }
}