package com.rubyhuntersky.vx.android.backingviews

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import com.rubyhuntersky.vx.android.tower.TowerAndroidView
import com.rubyhuntersky.vx.android.tower.ViewBackedTowerView
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BackingClickableView<Sight : Any, ClickContext : Any>
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    ViewBackedTowerView.BackingView<ClickEvent<ClickContext>> {

    fun enview(
        tower: Tower<Sight, Nothing>, id: ViewId, sightToClickContext: (Sight) -> ClickContext
    ) {
        removeAllViews()
        towerView = TowerAndroidView(context, tower, id)
            .apply {
                isClickable = true
                setBackgroundResource(
                    TypedValue()
                        .also { context.theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true) }
                        .resourceId
                )
                setOnClickListener {
                    sight?.let {
                        val single = ClickEvent.Single(sightToClickContext(it))
                        eventPublish.onNext(single)
                    }
                }
            }
        addView(
            towerView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        )
    }

    private lateinit var towerView: TowerAndroidView<Sight, Nothing>
    private val eventPublish: PublishSubject<ClickEvent<ClickContext>> = PublishSubject.create()
    private var sight: Sight? = null

    override val events: Observable<ClickEvent<ClickContext>> = eventPublish

    fun setSight(sight: Sight) {
        this.sight = sight
        towerView.setSight(sight)
    }

    override val heights: Observable<Int>
        get() = towerView.latitudes.map(Latitude::height).distinctUntilChanged()

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