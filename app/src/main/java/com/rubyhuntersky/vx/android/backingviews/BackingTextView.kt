package com.rubyhuntersky.vx.android.backingviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.android.coop.ViewBackedCoopView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.tower.AndroidTowerView
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.orbit.Orbit
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject

class BackingTextView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    TextView(context, attrs, defStyleAttr, defStyleRes),
    AndroidTowerView.BackingView<Nothing>,
    ViewBackedCoopView.BackingView<Nothing> {

    companion object : AndroidTowerView.Adapter<BackingTextView, WrapTextSight, Nothing> {

        override fun buildView(context: Context, viewId: ViewId) = BackingTextView(context)

        override fun renderView(view: BackingTextView, sight: WrapTextSight) {
            val resId = when (sight.style) {
                TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
                TextStyle.Subtitle2 -> R.style.TextAppearance_MaterialComponents_Subtitle2
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
    }

    override val events: Observable<Nothing>
        get() = Observable.never()

    override val heights: Observable<Int>
        get() = heightBehavior.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())

    private val heightBehavior = BehaviorSubject.create<Int>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        heightBehavior.onNext(toDip(h))
    }

    override var onAttached: (() -> Unit)? = null
    override var onDetached: (() -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached?.invoke()
    }

    override fun onDetachedFromWindow() {
        onDetached?.invoke()
        super.onDetachedFromWindow()
    }
}