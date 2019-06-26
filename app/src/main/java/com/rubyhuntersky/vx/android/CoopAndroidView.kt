package com.rubyhuntersky.vx.android

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.indexrebellion.presenters.cashediting.BackingViewTextView
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.BiBound
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.coop.Coop
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class CoopAndroidView<Sight : Any, Event : Any>(context: Context, coop: Coop<Sight, Event>, id: ViewId = ViewId()) :
    FrameLayout(context, null, 0, 0), Coop.ViewHost {

    init {
        setBackgroundColor(Color.LTGRAY)
    }

    private val boundUpdates = CompositeDisposable()
    private val boundBehavior: BehaviorSubject<BiBound> = BehaviorSubject.create()
    private val activeCoopView: Coop.View<Sight, Event> = coop.enview(this, id).also {
        restartBoundUpdates(it, isAttachedToWindow)
    }

    fun setSight(sight: Sight) {
        activeCoopView.setSight(sight)
    }

    private fun <C : Any, E : Any> restartBoundUpdates(coopView: Coop.View<C, E>?, isAttachedToWindow: Boolean) {
        boundUpdates.clear()
        if (coopView != null && isAttachedToWindow) {
            boundBehavior.distinctUntilChanged()
                .subscribe(coopView::setBound)
                .addTo(boundUpdates)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        restartBoundUpdates(activeCoopView, true)
    }

    override fun onDetachedFromWindow() {
        restartBoundUpdates(activeCoopView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        boundBehavior.onNext(BiBound(toDip(left), toDip(left + w), toDip(top), toDip(top + h)))
    }

    override fun addSingleTextLineView(textStyle: TextStyle, orbit: BiOrbit, id: ViewId): Coop.View<String, Nothing> =
        ViewBackedCoopView(
            frameLayout = this@CoopAndroidView,
            id = id,
            adapter = object : ViewBackedCoopView.Adapter<BackingViewTextView, String, Nothing> {

                override fun buildView(context: Context): BackingViewTextView = BackingViewTextView(context)

                override fun renderView(view: BackingViewTextView, sight: String) {
                    val resId = when (textStyle) {
                        TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                        TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                        TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
                        TextStyle.Body1 -> R.style.TextAppearance_MaterialComponents_Body1
                    }
                    view.setTextAppearance(resId)
                    view.textAlignment = when (orbit.hOrbit) {
                        Orbit.HeadLit -> View.TEXT_ALIGNMENT_TEXT_START
                        Orbit.TailLit -> View.TEXT_ALIGNMENT_TEXT_END
                        Orbit.Center -> View.TEXT_ALIGNMENT_CENTER
                        Orbit.HeadDim -> View.TEXT_ALIGNMENT_VIEW_START
                        Orbit.TailDim -> View.TEXT_ALIGNMENT_VIEW_END
                        is Orbit.Custom -> View.TEXT_ALIGNMENT_CENTER
                    }
                    view.gravity = when (orbit.vOrbit) {
                        Orbit.HeadLit -> Gravity.TOP
                        Orbit.TailLit -> Gravity.BOTTOM
                        Orbit.Center -> Gravity.CENTER_VERTICAL
                        Orbit.HeadDim -> Gravity.TOP
                        Orbit.TailDim -> Gravity.BOTTOM
                        is Orbit.Custom -> Gravity.CENTER_VERTICAL
                    }
                    view.text = sight
                }
            })

    override fun drop(id: ViewId) {
        (0 until childCount)
            .map(this::getChildAt)
            .filter { ViewBackedCoopView.isViewInGroup(it, id) }
            .forEach(this::removeView)
    }
}