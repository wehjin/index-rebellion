package com.rubyhuntersky.vx.android.backingviews

import android.R
import android.content.Context
import com.google.android.material.button.MaterialButton
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.rubyhuntersky.vx.android.coop.ViewBackedCoopView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.tower.AndroidTowerView
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight
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
    AndroidTowerView.BackingView<ClickEvent<Topic>>,
    ViewBackedCoopView.BackingView<ClickEvent<Topic>> {

    class Adapter<Topic : Any> : AndroidTowerView.Adapter<BackingButton<Topic>, ButtonSight<Topic>, ClickEvent<Topic>> {

        override fun buildView(context: Context, viewId: ViewId): BackingButton<Topic> =
            BackingButton(ContextThemeWrapper(context, R.style.Widget_Material_Button))

        override fun renderView(view: BackingButton<Topic>, sight: ButtonSight<Topic>) {
            view.text = sight.label
            view.topic = sight.topic
        }
    }


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