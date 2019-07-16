package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.robinhood.toHex
import kotlin.random.Random

object RbhDeviceTokenBuilder {

    private val segments = listOf(0..3, 4..5, 6..7, 8..9, 10..15)

    fun build(random: Random): String {

        val rands = (0 until 16)
            .map { i ->
                val r = random.nextDouble()
                val rand = 4294967296.0 * r
                val rightShift = (3 and i).shl(3)
                val value = rand.toLong().shr(rightShift) and 255
                value.toByte()
            }.toByteArray()

        return segments.map(rands::slice).joinToString("-", transform = List<Byte>::toHex)
    }
}
