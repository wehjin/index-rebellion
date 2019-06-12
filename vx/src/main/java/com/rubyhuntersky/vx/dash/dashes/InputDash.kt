package com.rubyhuntersky.vx.dash.dashes

import com.rubyhuntersky.vx.dash.Dash
import com.rubyhuntersky.vx.ViewId

data class InputSight(
    val text: String,
    val originalText: String,
    val label: String,
    val icon: Icon?
)

sealed class Icon {
    data class ResId(val resId: Int) : Icon()
}

sealed class InputEvent {
    data class TextChange(val text: String) : InputEvent()
}

object InputDash :
    Dash<InputSight, InputEvent> {
    override fun enview(viewHost: Dash.ViewHost, id: ViewId): Dash.View<InputSight, InputEvent> = viewHost.addInput(id)
}
