package com.rubyhuntersky.indexrebellion.common.views

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable

fun EditText.updateText(update: String, textWatcher: TextWatcher? = null) {
    textWatcher?.let(this::removeTextChangedListener)

    val old = text.toString()
    if (old != update) {
        setText(update)
        setSelection(update.length)
    }

    // Add watcher even if text has not changed. If not, the watcher is missing whenever this method
    // is used after removeTextChangedListener is separately called.
    textWatcher?.let(this::addTextChangedListener)
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