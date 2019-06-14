package com.rubyhuntersky.vx.android

import android.content.Context
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewInputLayout
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewTextView
import com.rubyhuntersky.indexrebellion.presenters.cashediting.ViewBackedDashView
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.TextWrapSight
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class TowerAndroidView<Sight : Any, Event : Any>(context: Context, tower: Tower<Sight, Event>, id: ViewId = ViewId()) :
    FrameLayout(context, null, 0, 0), Tower.ViewHost {

    private val hboundUpdates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()
    private val activeTowerView: Tower.View<Sight, Event> = tower.enview(this, id)
        .also {
            restartHBoundUpdates(it, isAttachedToWindow)
        }

    fun setSight(sight: Sight) {
        activeTowerView.setSight(sight)
    }

    private fun <C : Any, E : Any> restartHBoundUpdates(towerView: Tower.View<C, E>?, isAttachedToWindow: Boolean) {
        hboundUpdates.clear()
        if (towerView != null && isAttachedToWindow) {
            towerView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .subscribe {
                    towerView.setHBound(it.startZero())
                }
                .addTo(hboundUpdates)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        restartHBoundUpdates(activeTowerView, true)
    }

    override fun onDetachedFromWindow() {
        restartHBoundUpdates(activeTowerView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    override fun addInput(id: ViewId): Tower.View<InputSight, InputEvent> =
        ViewBackedDashView(
            frameLayout = this@TowerAndroidView,
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

    override fun addTextWrap(id: ViewId): Tower.View<TextWrapSight, Nothing> =
        ViewBackedDashView(
            frameLayout = this@TowerAndroidView,
            id = id,
            adapter = object :
                ViewBackedDashView.Adapter<BackingViewTextView, TextWrapSight, Nothing> {
                override fun buildView(context: Context): BackingViewTextView =
                    BackingViewTextView(context)

                override fun renderView(
                    view: BackingViewTextView,
                    content: TextWrapSight
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