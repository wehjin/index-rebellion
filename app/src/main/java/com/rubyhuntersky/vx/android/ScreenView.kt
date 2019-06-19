package com.rubyhuntersky.vx.android

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewInputLayout
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewTextView
import com.rubyhuntersky.indexrebellion.presenters.cashediting.ViewBackedTowerView
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

@Deprecated(message = "Use TowerAndroidView")
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

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> =
        ViewBackedTowerView(
            frameLayout = this@ScreenView,
            id = id,
            adapter = object : ViewBackedTowerView.Adapter<BackingViewInputLayout, InputSight, InputEvent> {
                override fun buildView(context: Context): BackingViewInputLayout {
                    return BackingViewInputLayout(context, null)
                }

                override fun renderView(view: BackingViewInputLayout, sight: InputSight) {
                    view.render(sight)
                }
            }
        )

    override fun addTextWrapView(id: ViewId): Tower.View<WrapTextSight, Nothing> =
        ViewBackedTowerView(
            frameLayout = this@ScreenView,
            id = id,
            adapter = object :
                ViewBackedTowerView.Adapter<BackingViewTextView, WrapTextSight, Nothing> {

                override fun buildView(context: Context): BackingViewTextView = BackingViewTextView(context)

                override fun renderView(view: BackingViewTextView, sight: WrapTextSight) {
                    val resId = when (sight.style) {
                        TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                        TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                        TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
                        TextStyle.Body1 -> R.style.TextAppearance_MaterialComponents_Body1
                    }
                    view.setTextAppearance(resId)
                    view.text = sight.text
                }
            })

    override fun drop(id: ViewId) {
        error("Not implemented, use TowerAndroidView")
    }
}