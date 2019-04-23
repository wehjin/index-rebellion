package com.rubyhuntersky.indexrebellion.common.views

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.updateText(update: String, textWatcher: TextWatcher) {
    val old = text.toString()
    if (old != update) {
        removeTextChangedListener(textWatcher)
        setText(update)
        setSelection(update.length)
        addTextChangedListener(textWatcher)
    }
}

interface SimpleTextWatcher : TextWatcher {
    fun textChanged(s: Editable)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable) = textChanged(s)
}