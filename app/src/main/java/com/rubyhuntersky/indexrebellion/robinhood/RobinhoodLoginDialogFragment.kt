package com.rubyhuntersky.indexrebellion.robinhood

import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.vxandroid.Renderer
import com.rubyhuntersky.indexrebellion.vxandroid.RendererBottomSheetDialogFragment

sealed class State {
    object None : State()
}

class RobinhoodLoginDialogFragment : RendererBottomSheetDialogFragment<State, Vision, Action>(Renderer(
    layoutRes = R.layout.view_robinhoodlogin,
    start = { State.None }
)) {
    companion object {
        fun new(interactionKey: Long): RobinhoodLoginDialogFragment = RobinhoodLoginDialogFragment()
            .also { it.indirectInteractionKey = interactionKey }
    }
}
