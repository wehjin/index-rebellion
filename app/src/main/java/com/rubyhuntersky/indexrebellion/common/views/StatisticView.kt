package com.rubyhuntersky.indexrebellion.common.views

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.rubyhuntersky.indexrebellion.R
import kotlinx.android.synthetic.main.view_statistic.view.*

class StatisticView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_statistic, this)
        attrs?.let { initAttributes(it, defStyleAttr, defStyleRes) }
    }

    private fun initAttributes(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.StatisticViewAttrs, defStyleAttr, defStyleRes)
            .apply {
                try {
                    text = getString(R.styleable.StatisticViewAttrs_text) ?:
                            context.getString(R.string.statisticViewDefaultValue)
                    labelText = getString(R.styleable.StatisticViewAttrs_labelText) ?:
                            context.getString(R.string.statisticViewDefaultUnit)
                    textColor = getColor(R.styleable.StatisticViewAttrs_textColor, Color.DKGRAY)
                } finally {
                    recycle()
                }
            }
    }

    var text: CharSequence
        get() = this.valueTextView.text
        set(value) {
            this.valueTextView.text = value
        }

    var textColor: Int
        get() = this.valueTextView.currentTextColor
        set(@ColorInt value) {
            this.valueTextView.setTextColor(value)
            this.labelTextView.setTextColor(value)
        }

    var labelText: CharSequence
        get() = this.labelTextView.text
        set(value) {
            this.labelTextView.text = value
        }
}