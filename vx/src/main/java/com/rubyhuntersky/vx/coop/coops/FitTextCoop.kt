package com.rubyhuntersky.vx.coop.coops

import com.rubyhuntersky.vx.common.TextStyle
import com.rubyhuntersky.vx.common.ViewId
import com.rubyhuntersky.vx.common.orbit.BiOrbit
import com.rubyhuntersky.vx.coop.Coop

class FitTextCoop(
    private val textStyle: TextStyle,
    private val orbit: BiOrbit
) : Coop<String, Nothing> {

    override fun enview(viewHost: Coop.ViewHost, id: ViewId): Coop.View<String, Nothing> {
        return viewHost.addFitTextView(textStyle, orbit, id)
    }
}
