package com.rubyhuntersky.vx.android.tower

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Space
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.Vx
import com.rubyhuntersky.vx.android.backingviews.BackingButton
import com.rubyhuntersky.vx.android.backingviews.BackingClickableView
import com.rubyhuntersky.vx.android.backingviews.BackingInputLayout
import com.rubyhuntersky.vx.android.backingviews.BackingTextView
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.toPixels
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Latitude
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.common.orbit.Orbit
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
    ConstraintLayout(context), Tower.ViewHost {

    init {
        this.id = this.hashCode()
        setBackgroundColor(Color.WHITE)
    }

    private val updates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()
    private val girder = Space(context).apply { id = hashCode() }.also { this.addView(it, 0, 0) }

    private val activeTowerView: Tower.View<Sight, Event> =
        tower.enview(this, viewId).also { manageUpdates(it, isAttachedToWindow) }

    override val events: Observable<Event> get() = activeTowerView.events
    override fun setSight(sight: Sight) = activeTowerView.setSight(sight)

    val latitudes: Observable<Latitude>
        get() = activeTowerView.latitudes

    private fun <C : Any, E : Any> manageUpdates(towerView: Tower.View<C, E>?, isAttachedToWindow: Boolean) {
        updates.clear()
        if (towerView != null && isAttachedToWindow) {
            towerView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .map(HBound::startZero)
                .subscribe(towerView::setHBound)
                .addTo(updates)
            latitudes.distinctUntilChanged()
                .subscribe {
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

    override fun drop(viewId: ViewId) {
        (0 until childCount)
            .map(this::getChildAt)
            .filter { AndroidTowerView.isViewInGroup(it, viewId) }
            .forEach(this::removeView)
    }

    override fun <Topic : Any> addTextInputView(id: ViewId): Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
        return object : Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {

            lateinit var topic: Topic

            private val core = AndroidTowerView(id,
                hostLayout = this@AndroidTowerViewHost,
                adapter = object : AndroidTowerView.Adapter<BackingInputLayout, InputSight, InputEvent> {
                    override fun buildView(context: Context): BackingInputLayout = BackingInputLayout(context)
                    override fun renderView(view: BackingInputLayout, sight: InputSight) = view.render(sight)
                }
            )

            override val events: Observable<TextInputEvent<Topic>>
                get() = core.events.map {
                    val (text) = it as InputEvent.TextChange
                    TextInputEvent.Changed(topic, text, text.length until text.length)
                }

            override fun setSight(sight: TextInputSight<Topic>) {
                topic = sight.topic
                core.setSight(InputSight(sight.type, sight.text, sight.hint, sight.label, null, sight.enabled))
            }

            override fun setHBound(hbound: HBound) {
                core.setHBound(hbound)
            }

            override val latitudes: Observable<Latitude> get() = core.latitudes
            override fun setAnchor(anchor: Anchor) = core.setAnchor(anchor)
        }
    }

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> =
        AndroidTowerView(id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = object : AndroidTowerView.Adapter<BackingInputLayout, InputSight, InputEvent> {
                override fun buildView(context: Context) = BackingInputLayout(context)
                override fun renderView(view: BackingInputLayout, sight: InputSight) = view.render(sight)
            }
        )

    override fun <Sight : Any, Topic : Any> addClickOverlayView(
        id: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ): Tower.View<Sight, ClickEvent<Topic>> =
        AndroidTowerView(id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = object :
                AndroidTowerView.Adapter<BackingClickableView<Sight, Topic>, Sight, ClickEvent<Topic>> {

                override fun buildView(context: Context): BackingClickableView<Sight, Topic> =
                    BackingClickableView<Sight, Topic>(context).also { it.enview(tower, id.extend(0), sightToTopic) }

                override fun renderView(view: BackingClickableView<Sight, Topic>, sight: Sight) {
                    view.setSight(sight)
                }
            }
        )

    override fun <Topic : Any> addClickView(id: ViewId): Tower.View<ClickSight<Topic>, ClickEvent<Topic>> =
        AndroidTowerView(id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = object : AndroidTowerView.Adapter<BackingButton<Topic>, ClickSight<Topic>, ClickEvent<Topic>> {

                override fun buildView(context: Context): BackingButton<Topic> =
                    BackingButton(ContextThemeWrapper(context, android.R.style.Widget_Material_Button))

                override fun renderView(view: BackingButton<Topic>, sight: ClickSight<Topic>) {
                    view.text = sight.label
                    view.topic = sight.topic
                }
            }
        )

    override fun addWrapTextView(
        id: ViewId
    ): Tower.View<WrapTextSight, Nothing> =
        AndroidTowerView(id,
            hostLayout = this@AndroidTowerViewHost,
            adapter = object : AndroidTowerView.Adapter<BackingTextView, WrapTextSight, Nothing> {

                override fun buildView(context: Context) = BackingTextView(context)

                override fun renderView(view: BackingTextView, sight: WrapTextSight) {
                    val resId = when (sight.style) {
                        TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                        TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                        TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
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
            })
}