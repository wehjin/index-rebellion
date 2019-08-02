package com.rubyhuntersky.vx.common

fun <T : Any> T?.toMortal(): Mortal<T> = if (this == null) Mortal.NotToBe else Mortal.Be(this)
fun <T : Any> T?.toDuality(): Duality<Unit, T> = if (this == null) Duality.Yin(Unit) else Duality.Yang(this)
