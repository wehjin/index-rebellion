package com.rubyhuntersky.vx.android.backingviews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.rubyhuntersky.vx.android.coop.ViewBackedCoopView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.tower.AndroidTowerView
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