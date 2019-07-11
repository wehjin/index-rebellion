package com.rubyhuntersky.vx.android.backingviews

import android.content.Context
import android.support.design.button.MaterialButton
import android.util.AttributeSet
import com.rubyhuntersky.vx.android.coop.ViewBackedCoopView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.tower.ViewBackedTowerView
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class BackingButton<Topic : Any>
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    MaterialButton(context, attrs, defStyleAttr),
    ViewBackedTowerView.BackingView<ClickEvent<Topic>>,
    ViewBackedCoopView.BackingView<ClickEvent<Topic>> {

    private val eventPublish: PublishSubject<ClickEvent<Topic>> = PublishSubject.create()
    var topic: Topic? = null

    init {
        setOnClickListener {
            topic?.let {
                eventPublish.onNext(ClickEvent.Single(it))
            }
        }
    }

    override val events: Observable<ClickEvent<Topic>> = eventPublish

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