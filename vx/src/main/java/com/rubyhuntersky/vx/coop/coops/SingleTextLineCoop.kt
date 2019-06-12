package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.coop.Coop

class SingleTextLineCoop(private val textStyle: TextStyle) :
    Coop<String, Nothing> {

    override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<String, Nothing> {
        return viewHost.addSingleTextLineView(textStyle, id)
    }
}