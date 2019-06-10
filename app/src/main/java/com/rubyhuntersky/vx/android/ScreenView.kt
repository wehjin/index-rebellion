package com.rubyhuntersky.vx.android

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewInputLayout
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewTextView
import com.rubyhuntersky.indexrebellion.presenters.cashediting.ViewBackedDashView
import com.rubyhuntersky.vx.*
import com.rubyhuntersky.vx.dashes.InputEvent
import com.rubyhuntersky.vx.dashes.InputSight
import com.rubyhuntersky.vx.dashes.TextLineSight
import com.rubyhuntersky.vx.dashes.TextStyle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class ScreenView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), ViewHost {

    fun <C : Any, E : Any> render(dashView: Dash.View<C, E>) {
        this.renderedDashView = dashView
        updateDashViewFromHBounds(renderedDashView, isAttachedToWindow)
    }

    private var renderedDashView: DashView<*, *>? = null

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

    override fun addTextLine(id: ViewId): DashView<TextLineSight, Nothing> =
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
                    when (content.style) {
                        TextStyle.Highlight5 -> view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5)
                        TextStyle.Highlight6 -> view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
                        TextStyle.Subtitle1 -> view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Subtitle1)
                    }
                    view.text = content.text
                }
            })
}