package com.rubyhuntersky.robinhood

fun List<Byte>.toHex(): String = joinToString("") { String.format("%02x", it) }
