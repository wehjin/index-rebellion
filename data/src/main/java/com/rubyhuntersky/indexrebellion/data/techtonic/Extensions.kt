package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Weight
import java.math.BigDecimal
import java.util.*

fun Date.later(rival: Date): Date = if (this.after(rival)) this else rival
fun Int.toWeight(): Weight = Weight(BigDecimal.valueOf(this.toLong()))
