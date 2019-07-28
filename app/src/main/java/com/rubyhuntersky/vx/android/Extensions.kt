package com.rubyhuntersky.vx.android

import android.content.Intent
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import com.rubyhuntersky.interaction.android.ActivityInteraction
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.edit.Validity
import com.rubyhuntersky.interaction.stringedit.StringEdit
import com.rubyhuntersky.vx.tower.towers.InputType
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight
import io.reactivex.Observable
import kotlin.math.roundToInt

@Suppress("unused")
fun Any.toUnit(): Unit = Unit

fun View.toPixels(dip: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), resources.displayMetrics)
}

fun View.toDip(px: Int): Int {
    return (px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun <Vision : Any, Action : Any> Interaction<Vision, Action>.logChanges(tag: String = this.group): Interaction<Vision, Action> {
    val core = this
    return object : Interaction<Vision, Action> {

        override val group: String
            get() = core.group

        override var edge: Edge
            get() = core.edge
            set(value) {
                core.edge = value
            }

        override val visions: Observable<Vision>
            get() = core.visions.doOnNext { println("$tag NEW VISION: $it") }

        override fun sendAction(action: Action) {
            println("$tag ACTION: $action VISION: ${core.visions.blockingFirst()}")
            core.sendAction(action)
        }

        override fun isEnding(someVision: Any?): Boolean = core.isEnding(someVision)
    }
}

fun Intent.putActivityInteractionSearchKey(key: Long): Intent = this.also {
    ActivityInteraction.setInteractionSearchKey(it, key)
}

fun <Topic : Any, T : Any> StringEdit<T>.toTextInputSight(
    type: InputType,
    topic: Topic,
    stringify: (T) -> String
): TextInputSight<Topic> {
    val text = novel?.component ?: ""
    val selection = novel?.selection ?: (text.length until text.length)
    val hint = ancient?.validValue?.let(stringify) ?: seed?.validValue?.let(stringify) ?: ""
    val error = (novel?.validity as? Validity.Invalid)?.reason ?: ""
    return TextInputSight(type, topic, text, selection, hint, label, error, enabled)
}
