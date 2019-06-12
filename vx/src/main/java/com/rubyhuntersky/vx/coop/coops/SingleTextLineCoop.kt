package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.TextStyle
import com.rubyhuntersky.vx.ViewId
import com.rubyhuntersky.vx.coop.Coop
import com.rubyhuntersky.vx.orbit.BiOrbit

class SingleTextLineCoop(
    private val textStyle: TextStyle,
    private val orbit: BiOrbit
) : Coop<String, Nothing> {

    override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<String, Nothing> {
        return viewHost.addSingleTextLineView(textStyle, orbit, id)
    }
}
