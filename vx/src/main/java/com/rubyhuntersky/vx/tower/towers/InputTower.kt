package com.rubyhuntersky.vx.tower.towers

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower

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

object InputTower :
    Tower<InputSight, InputEvent> {
    override fun enview(
        viewHost: Tower.ViewHost,
        id: ViewId
    ): Tower.View<InputSight, InputEvent> = viewHost.addInputView(id)
}
