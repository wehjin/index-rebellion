package com.rubyhuntersky.vx.android

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewInputLayout
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewTextView
import com.rubyhuntersky.indexrebellion.presenters.cashediting.ViewBackedDashView
import com.rubyhuntersky.vx.Anchor
import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.bounds.HBound
import com.rubyhuntersky.vx.dash.Dash
import com.rubyhuntersky.vx.dash.dashes.InputEvent
import com.rubyhuntersky.vx.dash.dashes.InputSight
import com.rubyhuntersky.vx.dash.dashes.TextLineSight
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class ScreenView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), Dash.ViewHost {

    fun <C : Any, E : Any> render(dashView: Dash.View<C, E>) {
        this.renderedDashView = dashView
        updateDashViewFromHBounds(renderedDashView, isAttachedToWindow)
    }

    private var renderedDashView: Dash.View<*, *>? = null

    private fun <C : Any, E : Any> updateDashViewFromHBounds(dashView: Dash.View<C, E>?, isAttachedToWindow: Boolean) {
        dashViewHBoundUpdates.clear()
        if (dashView != null && isAttachedToWindow) {
            dashView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .subscribe {
                    dashView.setHBound(it.startZero())
                }
                .addTo(dashViewHBoundUpdates)
        }
    }

    private val dashViewHBoundUpdates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateDashViewFromHBounds(renderedDashView, true)
    }

    override fun onDetachedFromWindow() {
        updateDashViewFromHBounds(renderedDashView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    override fun addInput(id: ViewId): Dash.View<InputSight, InputEvent> =
        ViewBackedDashView(
            frameLayout = this@ScreenView,
            id = id,
            adapter = object : ViewBackedDashView.Adapter<BackingViewInputLayout, InputSight, InputEvent> {
                override fun buildView(context: Context): BackingViewInputLayout {
                    return BackingViewInputLayout(context, null)
                }

                override fun renderView(view: BackingViewInputLayout, content: InputSight) {
                    view.render(content)
                }
            }
        )

    override fun addTextLine(id: ViewId): Dash.View<TextLineSight, Nothing> =
        ViewBackedDashView(
            frameLayout = this@ScreenView,
            id = id,
            adapter = object :
                ViewBackedDashView.Adapter<BackingViewTextView, TextLineSight, Nothing> {
                override fun buildView(context: Context): BackingViewTextView =
                    BackingViewTextView(context)

                override fun renderView(
                    view: BackingViewTextView,
                    content: TextLineSight
                ) {
                    val resId = when (content.style) {
                        TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                        TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                        TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
                        TextStyle.Body1 -> R.style.TextAppearance_MaterialComponents_Body1
                    }
                    view.setTextAppearance(resId)
                    view.text = content.text
                }
            })
}