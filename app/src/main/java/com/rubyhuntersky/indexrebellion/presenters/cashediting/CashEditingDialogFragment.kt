package com.rubyhuntersky.indexrebellion.presenters.cashediting

import android.os.Bundle
import android.util.Log
import android.view.View
import com.rubyhuntersky.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.vxandroid.InteractionBottomSheetDialogFragment
import com.rubyhuntersky.interaction.cashediting.Action
import com.rubyhuntersky.interaction.cashediting.Vision
import com.rubyhuntersky.vx.Dash
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.additions.Bottom
import com.rubyhuntersky.vx.additions.Gap
import com.rubyhuntersky.vx.additions.plus
import com.rubyhuntersky.vx.dashes.*
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
        val targetInput: InputSight
    ) {
        fun toPair() = Pair(title, targetInput)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dash =
            TitleDash.neverEvent<InputEvent>() +
                    Gap.TitleBody +
                    Bottom(InputDash, FundingEditor::toPair)

        dashView = dash.enview(view.screenView, ViewId())
            .also {
                view.screenView.render(it)
            }
        dashView.events
            .subscribe {
                sendAction(Action.SetEdit((it as InputEvent.TextChange).text))
            }
            .addTo(composite)

        saveButton.setOnClickListener {
            sendAction(Action.Save)
        }
    }

    private val composite = CompositeDisposable()
    private lateinit var dashView: Dash.View<FundingEditor, InputEvent>

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
            targetInput = InputSight(
                text = vision.edit,
                originalText = (if (vision.oldCashAmount < CashAmount.ZERO) "-" else "") + vision.oldCashAmount.toStatString(),
                label = getString(labelRes),
                icon = Icon.ResId(R.drawable.ic_attach_money_black_24dp)
            )
        )
        dashView.setSight(content)
        saveButton.isEnabled = vision.canSave
    }

    companion object {
        fun newInstance(): CashEditingDialogFragment = CashEditingDialogFragment()
    }
}