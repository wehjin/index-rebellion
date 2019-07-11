package com.rubyhuntersky.vx.android.tower

import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.android.backingviews.BackingButton
import com.rubyhuntersky.vx.android.backingviews.BackingClickableView
import com.rubyhuntersky.vx.android.backingviews.BackingInputLayout
import com.rubyhuntersky.vx.android.backingviews.BackingTextView
import com.rubyhuntersky.vx.android.toDip
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

class TowerAndroidView<Sight : Any, Event : Any>(context: Context, tower: Tower<Sight, Event>, id: ViewId = ViewId()) :
    FrameLayout(context, null, 0, 0), Tower.ViewHost {

    init {
        setBackgroundColor(Color.WHITE)
    }

    private val hboundUpdates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()
    private val activeTowerView: Tower.View<Sight, Event> = tower.enview(this, id)
        .also {
            restartHBoundUpdates(it, isAttachedToWindow)
        }

    val events: Observable<Event>
        get() = activeTowerView.events

    fun setSight(sight: Sight) {
        activeTowerView.setSight(sight)
    }

    val latitudes: Observable<Latitude>
        get() = activeTowerView.latitudes

    private fun <C : Any, E : Any> restartHBoundUpdates(towerView: Tower.View<C, E>?, isAttachedToWindow: Boolean) {
        hboundUpdates.clear()
        if (towerView != null && isAttachedToWindow) {
            towerView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .subscribe {
                    towerView.setHBound(it.startZero())
                }
                .addTo(hboundUpdates)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        restartHBoundUpdates(activeTowerView, true)
    }

    override fun onDetachedFromWindow() {
        restartHBoundUpdates(activeTowerView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    override fun drop(id: ViewId) {
        (0 until childCount)
            .map(this::getChildAt)
            .filter { ViewBackedTowerView.isViewInGroup(it, id) }
            .forEach(this::removeView)
    }

    override fun <Topic : Any> addTextInputView(id: ViewId): Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
        return object : Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {

            lateinit var topic: Topic

            private val core = ViewBackedTowerView(id,
                frameLayout = this@TowerAndroidView,
                adapter = object : ViewBackedTowerView.Adapter<BackingInputLayout, InputSight, InputEvent> {
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
                core.setSight(InputSight(sight.type, sight.text, sight.hint, sight.label, null))
            }

            override fun setHBound(hbound: HBound) {
                core.setHBound(hbound)
            }

            override val latitudes: Observable<Latitude> get() = core.latitudes
            override fun setAnchor(anchor: Anchor) {
                core.setAnchor(anchor)
            }
        }
    }

    override fun addInputView(id: ViewId): Tower.View<InputSight, InputEvent> =
        ViewBackedTowerView(id,
            frameLayout = this@TowerAndroidView,
            adapter = object : ViewBackedTowerView.Adapter<BackingInputLayout, InputSight, InputEvent> {
                override fun buildView(context: Context) = BackingInputLayout(context)
                override fun renderView(view: BackingInputLayout, sight: InputSight) = view.render(sight)
            }
        )

    override fun <Sight : Any, Topic : Any> addClickOverlayView(
        id: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ): Tower.View<Sight, ClickEvent<Topic>> =
        ViewBackedTowerView(id,
            frameLayout = this@TowerAndroidView,
            adapter = object :
                ViewBackedTowerView.Adapter<BackingClickableView<Sight, Topic>, Sight, ClickEvent<Topic>> {

                override fun buildView(context: Context): BackingClickableView<Sight, Topic> =
                    BackingClickableView<Sight, Topic>(context).also { it.enview(tower, id.extend(0), sightToTopic) }

                override fun renderView(view: BackingClickableView<Sight, Topic>, sight: Sight) {
                    view.setSight(sight)
                }
            }
        )

    override fun <Topic : Any> addClickView(id: ViewId): Tower.View<ClickSight<Topic>, ClickEvent<Topic>> =
        ViewBackedTowerView(id,
            frameLayout = this@TowerAndroidView,
            adapter = object : ViewBackedTowerView.Adapter<BackingButton<Topic>, ClickSight<Topic>, ClickEvent<Topic>> {

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
        ViewBackedTowerView(id,
            frameLayout = this@TowerAndroidView,
            adapter = object : ViewBackedTowerView.Adapter<BackingTextView, WrapTextSight, Nothing> {

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