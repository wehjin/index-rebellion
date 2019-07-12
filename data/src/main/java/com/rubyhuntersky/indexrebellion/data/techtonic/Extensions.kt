package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElementId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Weight
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

fun Date.later(rival: Date): Date = if (this.after(rival)) this else rival
fun Int.toWeight(): Weight = Weight(BigDecimal.valueOf(this.toLong()))

fun BigDecimal.toPortion(aggregate: BigDecimal): Double {
    check((aggregate >= BigDecimal.ZERO))
    check(aggregate >= this)
    return kotlin.math.min(1.0, divide(aggregate, 20, RoundingMode.HALF_UP).toDouble())
}

fun Plate.toDivisionElementId(): DivisionElementId = DivisionElementId.ForPlate(this)
fun DivisionId.toDivisionElementId(): DivisionElementId = DivisionElementId.ForDivision(this)


