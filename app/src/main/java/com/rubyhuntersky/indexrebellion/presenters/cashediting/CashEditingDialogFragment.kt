package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Vision
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.vx.android.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.mapSight
import com.rubyhuntersky.vx.tower.towers.EmptyTower
import com.rubyhuntersky.vx.tower.towers.Icon
import com.rubyhuntersky.vx.tower.towers.InputType
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputTower
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_cash_editing.*
import kotlinx.android.synthetic.main.fragment_cash_editing.view.*

class CashEditingDialogFragment : InteractionBottomSheetDialogFragment<Vision, Action>(
    layoutRes = R.layout.fragment_cash_editing,
    directInteraction = SharedCashEditingInteraction
) {
    data class FundingEditor(
        val title: String,
        val targetInput: TextInputSight<Unit>
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textInput = TextInputTower<Unit>().mapSight(FundingEditor::targetInput)
        val title = Standard.TitleTower().neverEvent<TextInputEvent<Unit>>().mapSight(FundingEditor::title)
        val tower = title and EmptyTower(Standard.titleToBodySpacing) and textInput

        towerView = tower.enview(view.screenView, ViewId())
            .also {
                view.screenView.render(it)
            }
        towerView.events
            .subscribe {
                sendAction(Action.SetEdit((it as TextInputEvent.Changed<Unit>).text))
            }
            .addTo(composite)

        saveButton.setOnClickListener {
            sendAction(Action.Save)
        }
    }

    private val composite = CompositeDisposable()
    private lateinit var towerView: Tower.View<FundingEditor, TextInputEvent<Unit>>

    override fun onDestroyView() {
        composite.clear()
        super.onDestroyView()
    }

    override fun render(vision: Vision) {
        Log.d(this.javaClass.simpleName, "VISION: $vision")
        when (vision) {
            is Vision.Editing -> renderEditing(vision)
            is Vision.Idle -> dismiss()
        }
    }

    private fun renderEditing(vision: Vision.Editing) {
        val labelRes = if (vision.edit.isBlank()) {
            if (vision.oldCashAmount < CashAmount.ZERO) R.string.withdrawal else R.string.deposit
        } else {
            if (vision.edit.startsWith("-")) R.string.withdrawal else R.string.deposit
        }
        val content = FundingEditor(
            title = "Update Funding",
            targetInput = TextInputSight(
                type = InputType.SIGNED_DECIMAL,
                text = vision.edit,
                hint = (if (vision.oldCashAmount < CashAmount.ZERO) "-" else "") + vision.oldCashAmount.toStatString(),
                label = getString(labelRes),
                icon = Icon.ResId(R.drawable.ic_attach_money_black_24dp),
                topic = Unit
            )
        )
        towerView.setSight(content)
        saveButton.isEnabled = vision.canSave
    }

    companion object {
        fun newInstance(): CashEditingDialogFragment = CashEditingDialogFragment()
    }
}