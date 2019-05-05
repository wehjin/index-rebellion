package com.rubyhuntersky.indexrebellion.data

import kotlin.math.absoluteValue
import kotlin.math.roundToLong

fun Double.toStatString(): String {

    var thousandsMultiplier = 0
    var value = this.absoluteValue
    while (value > 1000) {
        thousandsMultiplier++
        value /= 1000
    }

    val valueString = when {
        value >= 100 -> value.roundToLong().toString()
        value >= 10 -> String.format("%.1f", value)
        value >= 1 -> String.format("%.2f", value)
        else -> "0"
    }

    val multiplierString = when (thousandsMultiplier) {
        0 -> ""
        1 -> "K"
        2 -> "M"
        3 -> "B"
        4 -> "T"
        5 -> "P"
        6 -> "E"
        7 -> "Z"
        8 -> "Y"
        else -> "e$thousandsMultiplier"
    }

    return "$valueString$multiplierString"
}