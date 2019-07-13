package com.rubyhuntersky.vx.android.coop

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.rubyhuntersky.vx.android.tower.TowerAndroidView
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class BackingScrollView<Sight : Any, Event : Any>
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    ScrollView(context, attrs, defStyleAttr, defStyleRes),
    ViewBackedCoopView.BackingView<Event> {

    fun enview(tower: Tower<Sight, Event>, id: ViewId) {
        removeAllViews()
        towerView = TowerAndroidView(context, tower, id)
        addView(towerView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private lateinit var towerView: TowerAndroidView<Sight, Event>
    private var eventUpdates: Disposable? = null

    fun setSight(sight: Sight) {
        towerView.setSight(sight)
    }

    private val eventPublish: PublishSubject<Event> = PublishSubject.create()

    override val events: Observable<Event> = eventPublish

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        eventUpdates = towerView.events.subscribe(eventPublish::onNext, eventPublish::onError)
    }

    override fun onDetachedFromWindow() {
        eventUpdates?.dispose()
        super.onDetachedFromWindow()
    }
}