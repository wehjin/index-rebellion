package com.rubyhuntersky.robinhood.login

import androidx.fragment.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.common.views.SimpleTextWatcher
import com.rubyhuntersky.indexrebellion.common.views.updateText
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.Renderer
import com.rubyhuntersky.vx.android.RendererBottomSheetDialogFragment
import com.rubyhuntersky.vx.android.UpdateResult
import kotlinx.android.synthetic.main.view_robinhoodlogin.view.*

data class RendererData(
    val userTextWatcher: TextWatcher,
    val passwordTextWatcher: TextWatcher,
    val mfaTextWatcher: TextWatcher
)

class RobinhoodLoginDialogFragment : RendererBottomSheetDialogFragment<Vision, Action, RendererData>(
    object : Renderer<Vision, Action, RendererData> {
        override val layoutRes: Int = R.layout.view_robinhoodlogin

        override fun start(view: View, sendAction: (Action) -> Unit): RendererData {
            return RendererData(
                userTextWatcher = object : SimpleTextWatcher {
                    override fun textChanged(s: Editable) =
                        sendAction(Action.SetUsername(s.toString()))
                },
                passwordTextWatcher = object : SimpleTextWatcher {
                    override fun textChanged(s: Editable) =
                        sendAction(Action.SetPassword(s.toString()))
                },
                mfaTextWatcher = object : SimpleTextWatcher {
                    override fun textChanged(s: Editable) =
                        sendAction(Action.SetMfa(s.toString()))
                }
            ).also {
                view.userEditText.addTextChangedListener(it.userTextWatcher)
                view.passwordEditText.addTextChangedListener(it.passwordTextWatcher)
                view.mfaEditText.addTextChangedListener(it.mfaTextWatcher)
                view.signInButton.setOnClickListener { sendAction(Action.Submit) }
            }
        }

        override fun end(view: View, data: RendererData) {
            view.userEditText.removeTextChangedListener(data.userTextWatcher)
            view.passwordEditText.removeTextChangedListener(data.passwordTextWatcher)
            view.mfaEditText.removeTextChangedListener(data.mfaTextWatcher)
            super.end(view, data)
        }

        override fun update(
            vision: Vision,
            sendAction: (Action) -> Unit,
            view: View,
            data: RendererData
        ): UpdateResult<RendererData> {
            Log.d(ROBINHOOD_LOGIN, "VISION: $vision")
            return when (vision) {
                is Vision.Editing -> {
                    with(view.userEditText) {
                        updateText(vision.username, data.userTextWatcher)
                        isEnabled = true
                    }
                    with(view.passwordEditText) {
                        updateText(vision.password, data.passwordTextWatcher)
                        error = if (vision.error.isBlank()) null else vision.error
                        isEnabled = true
                        visibility = View.VISIBLE
                    }
                    with(view.mfaEditText) {
                        updateText(vision.mfa, data.mfaTextWatcher)
                        isEnabled = true
                        visibility = View.VISIBLE
                    }
                    with(view.signInButton) {
                        isEnabled = vision.submittable
                        visibility = View.VISIBLE
                    }
                    UpdateResult.Continue(data)
                }
                is Vision.Submitting -> {
                    view.userEditText.isEnabled = false
                    with(view.passwordEditText) {
                        isEnabled = false
                        visibility = View.VISIBLE
                    }
                    with(view.mfaEditText) {
                        isEnabled = false
                        visibility = View.VISIBLE
                    }
                    view.signInButton.visibility = View.INVISIBLE
                    UpdateResult.Continue(data)
                }
                is Vision.Reporting -> {
                    view.userEditText.isEnabled = false
                    view.passwordEditText.visibility = View.INVISIBLE
                    view.mfaEditText.visibility = View.INVISIBLE
                    view.signInButton.visibility = View.INVISIBLE
                    Log.d(ROBINHOOD_LOGIN, "REPORT: $vision")
                    UpdateResult.Finish(data)
                }
            }
        }
    }
) {
    companion object : ProjectionSource<Vision, Action> {
        override val group: String = ROBINHOOD_LOGIN

        override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) {
            RobinhoodLoginDialogFragment()
                .apply { indirectInteractionKey = key }
                .show(activity.supportFragmentManager, "$ROBINHOOD_LOGIN/Projection")
        }
    }
}
