package com.rubyhuntersky.vx.android.tower

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.Log
import android.view.View
import android.widget.Space
import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.android.backingviews.BackingButton
import com.rubyhuntersky.vx.android.backingviews.BackingClickableView
import com.rubyhuntersky.vx.android.backingviews.BackingInputLayout
import com.rubyhuntersky.vx.android.backingviews.BackingTextView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.toPixels
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ClickSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class AndroidTowerViewHost<in Sight : Any, Event : Any>(
    context: Context,
    tower: Tower<Sight, Event>,
    viewId: ViewId = ViewId()
) :
    Vx<Sight, Event>,
    ConstraintLayout(context),
    Tower.ViewHost {

    init {
        setBackgroundColor(Color.WHITE)
    }

    private val updates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()
    private val girder = Space(context).apply { id = hashCode() }.also { this.addView(it, 0, 0) }

    private val activeTowerView: Tower.View<Sight, Event> =
        tower.enview(this, viewId).also { manageUpdates(it, isAttachedToWindow) }

    override val events: Observable<Event> get() = activeTowerView.events
    override fun setSight(sight: Sight) = activeTowerView.setSight(sight)

    val latitudes: Observable<Latitude> get() = latitudeBehavior
    private val latitudeBehavior: BehaviorSubject<Latitude> = BehaviorSubject.create()

    private fun <C : Any, E : Any> manageUpdates(towerView: Tower.View<C, E>?, isAttachedToWindow: Boolean) {
        updates.clear()
        if (towerView != null && isAttachedToWindow) {
            towerView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .map(HBound::startZero)
                .subscribe(towerView::setHBound)
                .addTo(updates)
            activeTowerView.latitudes.distinctUntilChanged()
                .subscribe {
                    latitudeBehavior.onNext(it)
                    ConstraintSet().apply {
                        clone(this@AndroidTowerViewHost)
                        constrainHeight(girder.id, toPixels(it.height).toInt())
                        connect(girder.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                        applyTo(this@AndroidTowerViewHost)
                    }
                    requestLayout()
                }
                .addTo(updates)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        manageUpdates(activeTowerView, true)
    }

    override fun onDetachedFromWindow() {
        manageUpdates(activeTowerView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    private var freeViews = emptySet<View>()
    private val recycledViews = mutableSetOf<View>()

    override fun drop(viewId: ViewId, start: Boolean) {
        if (start) {
            recycledViews.clear()
            freeViews = (0 until childCount)
                .map(this::getChildAt)
                .filter { AndroidTowerView.isViewInGroup(it, viewId) }
                .toSet()
            Log.d("DROP", "START: ${freeViews.size} marked")
        } else {
            val subtract = freeViews.subtract(recycledViews)
            subtract.forEach(this::removeView)
            Log.d("DROP", "END: ${recycledViews.size} recycled, ${subtract.size} freed")
            freeViews = emptySet()
            recycledViews.clear()
        }
    }

    private fun onRecycledView(view: View) {
        if (freeViews.isNotEmpty()) {
            recycledViews.add(view)
        }
    }

    override fun <Topic : Any> addTextInputView(id: ViewId): Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
        return object : Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {

            override fun dequeue() = core.dequeue()

            lateinit var topic: Topic

            private val core = AndroidTowerView(
                id,
                hostLayout = this@AndroidTowerViewHost,
                adapter = BackingInputLayout,
                onRecycledView = this@AndroidTowerViewHost::onRecycledView
            )

            override val events: Observable<TextInputEvent<Topic>>
                get() = core.events.map {
                    val (text) = it as InputEvent.TextChange
                    TextInputEvent.Changed(topic, text, text.length until text.length)
                }

            override fun setSight(sight: TextInputSight<Topic>) {
                topic = sight.topic
                core.setSight(InputSight(sight.type, sight.text, sight.hint, sight.label, sight.icon, sight.enabled))
            }

            override fun setHBound(hbound: HBound) {
                core.setHBound(hbound)
            }

            override val latitudes: Observable<Latitude> get() = core.latitudes
            override fun setAnchor(anchor: Anchor) = core.setAnchor(anchor)
        }
    }

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> =
        AndroidTowerView(
            id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = BackingInputLayout,
            onRecycledView = this::onRecycledView
        )

    override fun <Sight : Any, Topic : Any> addClickOverlayView(
        id: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ): Tower.View<Sight, ClickEvent<Topic>> =
        AndroidTowerView(
            id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = BackingClickableView.Adapter(tower, sightToTopic),
            onRecycledView = this::onRecycledView
        )

    override fun <Topic : Any> addClickView(id: ViewId): Tower.View<ClickSight<Topic>, ClickEvent<Topic>> =
        AndroidTowerView(
            id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = BackingButton.Adapter<Topic>(),
            onRecycledView = this::onRecycledView
        )

    override fun addWrapTextView(id: ViewId): Tower.View<WrapTextSight, Nothing> =
        AndroidTowerView(
            id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = BackingTextView,
            onRecycledView = this::onRecycledView
        )
}