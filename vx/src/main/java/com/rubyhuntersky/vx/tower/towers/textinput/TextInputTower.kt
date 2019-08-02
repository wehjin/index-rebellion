package com.rubyhuntersky.vx.tower.towers.textinput

import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputEvent as Event
import com.rubyhuntersky.vx.tower.towers.textinput.TextInputSight as Sight

class TextInputTower<Topic : Any> : Tower<Sight<Topic>, Event<Topic>> {
    override fun enview(
        viewHost: Tower.ViewHost,
        viewId: ViewId
    ): Tower.View<Sight<Topic>, Event<Topic>> = viewHost.addTextInputView(viewId)
}