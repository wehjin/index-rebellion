package com.rubyhuntersky.indexrebellion.presenters

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory.Action
import com.rubyhuntersky.indexrebellion.interactions.ChooseHoldingTypeStory.Vision
import com.rubyhuntersky.indexrebellion.interactions.HoldingType
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.InteractionBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_choose_holding_type.*

class ChooseHoldingTypeFragment() :
    InteractionBottomSheetDialogFragment<Vision, Action>(
        layoutRes = R.layout.fragment_choose_holding_type,
        directInteraction = null
    ) {

    override fun render(vision: Vision) {
        dollarsTextView.setOnClickListener {
            sendAction(Action.Choose(HoldingType.DOLLARS))
            dismiss()
        }
        stocksTextView.setOnClickListener {
            sendAction(Action.Choose(HoldingType.STOCKS))
            dismiss()
        }
    }

    companion object : ProjectionSource<Vision, Action> {
        override val group: String = ChooseHoldingTypeStory.groupId

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) =
            ChooseHoldingTypeFragment().startInActivity(activity, key)
    }
}