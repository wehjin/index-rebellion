package com.rubyhuntersky.vx.android.backingviews

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import com.rubyhuntersky.vx.android.toDip
import com.rubyhuntersky.vx.android.tower.ViewBackedTowerView
import com.rubyhuntersky.vx.tower.towers.Icon
import com.rubyhuntersky.vx.tower.towers.InputEvent
import com.rubyhuntersky.vx.tower.towers.InputSight
import com.rubyhuntersky.vx.tower.towers.InputType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.vxa_view_input.view.*

class BackingInputLayout
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), ViewBackedTowerView.BackingView<InputEvent> {

    private var layout: TextInputLayout

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.vxa_view_input, this, false)
        addView(view, ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        layout = view as TextInputLayout
    }

    override val events: Observable<InputEvent>
        get() = eventPublish.observeOn(AndroidSchedulers.mainThread())

    private val eventPublish = PublishSubject.create<InputEvent>()

    private var activeInputType: InputType? = null
        set(value) {
            if (field != value) {
                field = value.also {
                    editText.inputType = when (it) {
                        InputType.SIGNED_DECIMAL -> EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_SIGNED or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                        InputType.UNSIGNED_DECIMAL -> EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                        InputType.WORD -> EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        null -> EditorInfo.TYPE_CLASS_TEXT
                    }
                }
            }
        }

    fun render(content: InputSight) {
        Log.d(this.tag.toString(), "render $content")
        activeInputType = content.type

        if (content.enabled) {
            fun renderEnabledHints(hasFocus: Boolean) {
                layout.hint = content.label
                if (hasFocus) {
                    editText.hint = content.originalText
                } else {
                    editText.hint = null
                }
            }
            renderEnabledHints(editText.hasFocus())
            editText.setOnFocusChangeListener { _, hasFocus -> renderEnabledHints(hasFocus) }
        } else {
            fun renderDisabledHints(hasFocus: Boolean) {
                if (hasFocus) {
                    layout.hint = content.label
                    editText.hint = content.originalText
                } else {
                    layout.hint = "${content.label}: ${content.originalText}"
                    editText.hint = null
                }
            }
            renderDisabledHints(editText.hasFocus())
            editText.setOnFocusChangeListener { _, hasFocus -> renderDisabledHints(hasFocus) }
        }
        editText.isEnabled = content.enabled

        if (editText.text.toString() != content.text) {
            editText.setText(content.text)
        }
        val drawable = (content.icon as? Icon.ResId)?.let {
            resources.getDrawable(it.resId, context.theme)
        }
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    override val heights: Observable<Int>
        get() = heightBehavior.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())

    private val heightBehavior = BehaviorSubject.create<Int>()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val w = right - left
        val h = bottom - top
        Log.d(this.tag.toString(), "onSizeChanged w x h: $w x $h")
        heightBehavior.onNext(toDip(h))
    }

    override var onAttached: (() -> Unit)? = null

    override var onDetached: (() -> Unit)? = null

    private val editText by lazy { layout.inputEditText }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttached?.invoke()
        editText.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            eventPublish.onNext(InputEvent.TextChange(s?.toString() ?: ""))
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    override fun onDetachedFromWindow() {
        editText.removeTextChangedListener(textWatcher)
        onDetached?.invoke()
        super.onDetachedFromWindow()
    }

}