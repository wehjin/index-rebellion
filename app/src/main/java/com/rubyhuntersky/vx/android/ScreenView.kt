package com.rubyhuntersky.vx.android

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.android.backingviews.BackingInputLayout
import com.rubyhuntersky.vx.android.backingviews.BackingTextView
import com.rubyhuntersky.vx.android.tower.AndroidTowerView
import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.Height
import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.bound.HBound
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.click.ClickEvent
import com.rubyhuntersky.vx.tower.towers.click.ButtonSight
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import com.rubyhuntersky.vx.tower.towers.wraptext.WrapTextSight
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

@Deprecated(message = "Use TowerAndroidView")
class ScreenView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    Tower.ViewHost,
    ConstraintLayout(context, attrs, defStyleAttr) {

    fun <C : Any, E : Any> render(towerView: Tower.View<C, E>) {
        this.renderedTowerView = towerView
        updateDashViewFromHBounds(renderedTowerView, isAttachedToWindow)
    }

    private var renderedTowerView: Tower.View<*, *>? = null

    private fun <C : Any, E : Any> updateDashViewFromHBounds(
        towerView: Tower.View<C, E>?,
        isAttachedToWindow: Boolean
    ) {
        viewHBoundUpdates.clear()
        if (towerView != null && isAttachedToWindow) {
            towerView.setAnchor(Anchor(0, 0f))
            hboundBehavior.distinctUntilChanged()
                .subscribe {
                    towerView.setHBound(it.startZero())
                }
                .addTo(viewHBoundUpdates)
        }
    }

    private val viewHBoundUpdates = CompositeDisposable()
    private val hboundBehavior: BehaviorSubject<HBound> = BehaviorSubject.create()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateDashViewFromHBounds(renderedTowerView, true)
    }

    override fun onDetachedFromWindow() {
        updateDashViewFromHBounds(renderedTowerView, false)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hboundBehavior.onNext(HBound(toDip(left), toDip(left + w)))
    }

    override fun drop(viewId: ViewId, start: Boolean) {
        error("Not implemented, use TowerAndroidView")
    }

    override fun <Topic : Any> addTextInputView(id: ViewId): Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {
        return object : Tower.View<TextInputSight<Topic>, TextInputEvent<Topic>> {

            override fun drop() = core.drop()

            lateinit var topic: Topic

            private val core = AndroidTowerView(
                id,
                hostLayout = this@ScreenView,
                adapter = BackingInputLayout
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

            override val latitudes: Observable<Height> get() = core.latitudes
            override fun setAnchor(anchor: Anchor) = core.setAnchor(anchor)
        }
    }

    override fun <Sight : Any, Topic : Any> addClickOverlayView(
        id: ViewId,
        tower: Tower<Sight, Nothing>,
        sightToTopic: (Sight) -> Topic
    ): Tower.View<Sight, ClickEvent<Topic>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <Topic : Any> addButtonView(id: ViewId): Tower.View<ButtonSight<Topic>, ClickEvent<Topic>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addWrapTextView(id: ViewId): Tower.View<WrapTextSight, Nothing> =
        AndroidTowerView(
            hostLayout = this@ScreenView,
            viewId = id,
            adapter = object : AndroidTowerView.Adapter<BackingTextView, WrapTextSight, Nothing> {
                override fun buildView(context: Context, viewId: ViewId): BackingTextView = BackingTextView(context)

                override fun renderView(view: BackingTextView, sight: WrapTextSight) {
                    val resId = when (sight.style) {
                        TextStyle.Highlight5 -> R.style.TextAppearance_MaterialComponents_Headline5
                        TextStyle.Highlight6 -> R.style.TextAppearance_MaterialComponents_Headline6
                        TextStyle.Subtitle1 -> R.style.TextAppearance_MaterialComponents_Subtitle1
                        TextStyle.Subtitle2 -> R.style.TextAppearance_MaterialComponents_Subtitle2
                        TextStyle.Body1 -> R.style.TextAppearance_MaterialComponents_Body1
                    }
                    view.setTextAppearance(resId)
                    view.text = sight.text
                }
            })
}