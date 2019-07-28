package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Vision
import com.rubyhuntersky.vx.android.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.Bottom
import com.rubyhuntersky.vx.tower.additions.Gap
import com.rubyhuntersky.vx.tower.additions.plus
import com.rubyhuntersky.vx.tower.towers.Icon
import com.rubyhuntersky.vx.tower.towers.InputType
import com.rubyhuntersky.vx.tower.towers.TitleTower
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
    ) {
        fun toPair() = Pair(title, targetInput)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textInputTower = TextInputTower<Unit>()
        val tower =
            TitleTower.neverEvent<TextInputEvent<Unit>>() +
                    Gap.TitleBody +
                    Bottom(textInputTower, FundingEditor::toPair)

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