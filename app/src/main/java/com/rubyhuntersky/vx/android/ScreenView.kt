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
import com.rubyhuntersky.vx.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrap
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class ScreenView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), Tower.ViewHost {

    fun <C : Any, E : Any> render(towerView: Tower.View<C, E>) {
        this.renderedTowerView = towerView
        updateDashViewFromHBounds(renderedTowerView, isAttachedToWindow)
    }

    private var renderedTowerView: Tower.View<*, *>? = null

    private fun <C : Any, E : Any> updateDashViewFromHBounds(
        towerView: Tower.View<C, E>?,
        isAttachedToWindow: Boolean
    ) {
        viewHBoundUpdates.clear()
        if (towerView != null && isAttachedToWindow) {
            towerView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .subscribe {
                    towerView.setHBound(it.startZero())
                }
                .addTo(viewHBoundUpdates)
        }
    }

    private val viewHBoundUpdates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateDashViewFromHBounds(renderedTowerView, true)
    }

    override fun onDetachedFromWindow() {
        updateDashViewFromHBounds(renderedTowerView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    override fun addInput(id: ViewId): Tower.View<InputSight, InputEvent> =
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

    override fun addTextWrap(id: ViewId): Tower.View<TextWrap, Nothing> =
        ViewBackedDashView(
            frameLayout = this@ScreenView,
            id = id,
            adapter = object :
                ViewBackedDashView.Adapter<BackingViewTextView, TextWrap, Nothing> {
                override fun buildView(context: Context): BackingViewTextView =
                    BackingViewTextView(context)

                override fun renderView(
                    view: BackingViewTextView,
                    content: TextWrap
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