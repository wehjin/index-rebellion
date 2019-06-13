package com.rubyhuntersky.vx.tower.additions

import com.rubyhuntersky.vx.Anchor
import io.reactivex.functions.BiFunction

data class SizeAnchor(val size: Int, val anchor: Anchor)

val toSizeAnchor = BiFunction(::SizeAnchor)
