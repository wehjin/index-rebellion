package com.rubyhuntersky.vx.android.backingviews

import android.R
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import com.rubyhuntersky.vx.android.tower.AndroidTowerView
import com.rubyhuntersky.vx.android.tower.AndroidTowerViewHost
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BackingClickableView<Sight : Any, Topic : Any>
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context, attrs, defStyleAttr),
    AndroidTowerView.BackingView<ClickEvent<Topic>> {

    class Adapter<Sight : Any, Topic : Any>(
        private val tower: Tower<Sight, Nothing>,
        private val sightToTopic: (Sight) -> Topic
    ) :
        AndroidTowerView.Adapter<BackingClickableView<Sight, Topic>, Sight, ClickEvent<Topic>> {

        override fun buildView(context: Context, viewId: ViewId): BackingClickableView<Sight, Topic> {
            return BackingClickableView<Sight, Topic>(context).apply { setup(viewId, tower, sightToTopic) }
        }

        override fun renderView(view: BackingClickableView<Sight, Topic>, sight: Sight) {
            view.setSight(sight)
        }
    }

    fun setup(
        viewId: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ) {
        removeAllViews()
        towerViewHost = AndroidTowerViewHost(context, tower, viewId.extend(1))
            .apply {
                isClickable = true
                setBackgroundResource(selectableItemBackground)
                setOnClickListener {
                    sight?.let {
                        val single = ClickEvent.Single(sightToTopic(it))
                        this@BackingClickableView.eventPublish.onNext(single)
                    }
                }
            }
        addView(
            towerViewHost,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        )
    }

    private val selectableItemBackground: Int
        get() = TypedValue()
            .also { context.theme.resolveAttribute(R.attr.selectableItemBackground, it, true) }
            .resourceId

    private lateinit var towerViewHost: AndroidTowerViewHost<Sight, Nothing>
    private val eventPublish: PublishSubject<ClickEvent<Topic>> = PublishSubject.create()
    private var sight: Sight? = null

    override val events: Observable<ClickEvent<Topic>> = eventPublish

    fun setSight(sight: Sight) {
        this.sight = sight
        towerViewHost.setSight(sight)
    }

    override val heights: Observable<Int>
        get() = towerViewHost.latitudes.map(Latitude::height).distinctUntilChanged()

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

