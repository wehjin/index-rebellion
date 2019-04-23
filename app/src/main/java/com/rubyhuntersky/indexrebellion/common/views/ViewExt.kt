package com.rubyhuntersky.indexrebellion.common.views

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable

fun EditText.updateText(update: String, textWatcher: TextWatcher? = null) {
    val old = text.toString()
    if (old != update) {
        textWatcher?.let {
            removeTextChangedListener(textWatcher)
        }
        setText(update)
        setSelection(update.length)
        textWatcher?.let {
            addTextChangedListener(textWatcher)
        }
    }
}

fun EditText.toTextChanges(): Observable<String> {
    return Observable.create { emitter ->
        val textWatcher = object : SimpleTextWatcher {
            override fun textChanged(s: Editable) = emitter.onNext(s.toString())
        }
        emitter.setCancellable { removeTextChangedListener(textWatcher) }
        addTextChangedListener(textWatcher)
    }
}

interface SimpleTextWatcher : TextWatcher {
    fun textChanged(s: Editable)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable) = textChanged(s)
}