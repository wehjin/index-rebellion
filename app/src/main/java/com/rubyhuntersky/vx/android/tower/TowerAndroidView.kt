package com.rubyhuntersky.vx.android.tower

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.android.backingviews.BackingInputLayout
import com.rubyhuntersky.vx.android.backingviews.BackingTextView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.textwrap.WrapTextSight
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class TowerAndroidView<Sight : Any, Event : Any>(context: Context, tower: Tower<Sight, Event>, id: ViewId = ViewId()) :
    FrameLayout(context, null, 0, 0), Tower.ViewHost {

    init {
        setBackgroundColor(Color.LTGRAY)
    }

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

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> {
        return ViewBackedTowerView(
            frameLayout = this@TowerAndroidView,
            id = id,
            adapter = object :
                ViewBackedTowerView.Adapter<BackingInputLayout, InputSight, InputEvent> {

                override fun buildView(context: Context): BackingInputLayout =
                    BackingInputLayout(context, null)

                override fun renderView(view: BackingInputLayout, sight: InputSight) {
                    view.render(sight)
                }
            }
        )
    }

    override fun addWrapTextView(id: ViewId): Tower.View<WrapTextSight, Nothing> {
        return ViewBackedTowerView(
            frameLayout = this@TowerAndroidView,
            id = id,
            adapter = object :
                ViewBackedTowerView.Adapter<BackingTextView, WrapTextSight, Nothing> {

                override fun buildView(context: Context): BackingTextView =
                    BackingTextView(context)

                override fun renderView(view: BackingTextView, sight: WrapTextSight) {
                    val resId = when (sight.style) {
                        TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                        TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                        TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
                        TextStyle.Body1 -> R.style.TextAppearance_MaterialComponents_Body1
                    }
                    view.setTextAppearance(resId)
                    view.textAlignment = when (sight.orbit) {
                        Orbit.HeadLit -> View.TEXT_ALIGNMENT_TEXT_START
                        Orbit.TailLit -> View.TEXT_ALIGNMENT_TEXT_END
                        Orbit.Center -> View.TEXT_ALIGNMENT_CENTER
                        Orbit.HeadDim -> View.TEXT_ALIGNMENT_VIEW_START
                        Orbit.TailDim -> View.TEXT_ALIGNMENT_VIEW_END
                        is Orbit.Custom -> View.TEXT_ALIGNMENT_CENTER
                    }
                    view.text = sight.text
                }
            })
    }

    override fun drop(id: ViewId) {
        (0 until childCount)
            .map(this::getChildAt)
            .filter { ViewBackedTowerView.isViewInGroup(it, id) }
            .forEach(this::removeView)
    }
}