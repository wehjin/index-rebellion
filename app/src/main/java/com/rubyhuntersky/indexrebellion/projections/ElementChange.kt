package com.rubyhuntersky.indexrebellion.projections

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElement
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElementId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Weight
import com.rubyhuntersky.interaction.edit.StringChange
import com.rubyhuntersky.interaction.edit.StringNovel
import com.rubyhuntersky.interaction.edit.Validity
import java.math.BigDecimal

data class ElementChange(
    val elementId: DivisionElementId,
    val stringChange: StringChange
) {

    private val string: String
        get() = stringChange.value

    private val validity: Validity<DivisionElement, String>
        get() {
            val string = stringChange.value
            val double = string.toDoubleOrNull()
            return if (double != null) {
                Validity.Valid(DivisionElement(elementId, Weight(BigDecimal.valueOf(double))))
            } else {
                Validity.Invalid(string, "Cannot convert to Double.")
            }
        }

    private val selection: IntRange
        get() = stringChange.selection

    fun toNovel(): StringNovel<DivisionElement> = StringNovel(string, validity, selection)
}