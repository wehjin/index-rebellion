package com.rubyhuntersky.indexrebellion.edit

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElement
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElementId
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Weight
import com.rubyhuntersky.interaction.edit.Novel
import com.rubyhuntersky.interaction.edit.Validity
import com.rubyhuntersky.interaction.stringedit.StringEdit
import java.math.BigDecimal

data class DivisionNovel(
    override val component: Map<DivisionElementId, String?>,
    override val validity: Validity<Division, Map<DivisionElementId, String?>>,
    override val selection: Map<DivisionElementId, IntRange?>
) : Novel<Division, Map<DivisionElementId, String?>, Map<DivisionElementId, IntRange?>> {

    companion object {

        fun fromElementEdits(
            elementEdits: Map<DivisionElementId, StringEdit<DivisionElement>>,
            ancientDivision: Division
        ): DivisionNovel? {
            val elementNovels = elementEdits.mapValues { (_, elementEdit) -> elementEdit.novel }
            val novelCount = elementNovels.values.fold(0, { count, novel -> count + if (novel == null) 0 else 1 })
            return when {
                novelCount > 0 -> {
                    val component = elementNovels.mapValues { (_, novel) -> novel?.component }
                    val selection = elementNovels.mapValues { (_, novel) -> novel?.selection }

                    val allValidOrNotNovel = elementNovels
                        .mapValues { (_, novel) -> novel?.validity?.isValid ?: true }
                        .values
                        .fold(true, { a, b -> a && b })

                    val validity: Validity<Division, Map<DivisionElementId, String?>> =
                        if (allValidOrNotNovel) {

                            val changedElements = component.mapValues { (id, string) ->
                                string?.let {
                                    val weight = Weight(BigDecimal.valueOf(string.toDouble()))
                                    DivisionElement(id, weight)
                                }
                            }

                            val nonNull = mutableMapOf<DivisionElementId, DivisionElement>()
                            changedElements.entries.forEach { (id, element) ->
                                if (element != null) {
                                    nonNull[id] = element
                                }
                            }

                            Validity.Valid(ancientDivision.replace(nonNull.values.toList()))
                        } else Validity.Invalid(component, "not all components are valid")

                    DivisionNovel(component, validity, selection)
                }
                else -> null
            }
        }
    }
}
