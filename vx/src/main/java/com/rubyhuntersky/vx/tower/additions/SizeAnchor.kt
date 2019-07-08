package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.common.Anchor
import com.rubyhuntersky.vx.common.bound.VBound
import io.reactivex.functions.BiFunction

data class SizeAnchor(val size: Int, val anchor: Anchor) {

    val vBound: VBound by lazy { anchor.toVBound(size) }
}

val toSizeAnchor = BiFunction(::SizeAnchor)
