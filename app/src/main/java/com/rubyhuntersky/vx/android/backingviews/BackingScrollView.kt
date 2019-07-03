package com.rubyhuntersky.vx.android.backingviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ScrollView
import com.rubyhuntersky.vx.android.coop.ViewBackedCoopView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.tower.TowerAndroidView
import com.rubyhuntersky.vx.android.tower.ViewBackedTowerView
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class BackingScrollView<Sight : Any, Event : Any>
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    ScrollView(context, attrs, defStyleAttr, defStyleRes),
    ViewBackedTowerView.BackingView<Event>,
    ViewBackedCoopView.BackingView<Event> {

    fun enview(tower: Tower<Sight, Event>, id: ViewId) {
        removeAllViews()
        towerView = TowerAndroidView(context, tower, id)
        addView(towerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private lateinit var towerView: TowerAndroidView<Sight, Event>
    private var eventUpdates: Disposable? = null

    fun setSight(sight: Sight) {
        towerView.setSight(sight)
    }

    private val eventPublish: PublishSubject<Event> = PublishSubject.create()

    override val events: Observable<Event> = eventPublish

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
        eventUpdates = towerView.events.subscribe(eventPublish::onNext, eventPublish::onError)
    }

    override fun onDetachedFromWindow() {
        eventUpdates?.dispose()
        onDetached?.invoke()
        super.onDetachedFromWindow()
    }
}